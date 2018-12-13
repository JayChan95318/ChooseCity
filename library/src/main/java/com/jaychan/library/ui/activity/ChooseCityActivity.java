package com.jaychan.library.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jaychan.library.R;
import com.jaychan.library.bean.City;
import com.jaychan.library.constants.Constants;
import com.jaychan.library.db.CityDao;
import com.jaychan.library.event.ChooseCityEvent;
import com.jaychan.library.ui.adapter.CityAdapter;
import com.jaychan.library.ui.view.QuickIndexBar;
import com.jaychan.library.utils.PreUtils;
import com.jaychan.library.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author JayChan
 * @date 2018/12/13  14:54
 */
public class ChooseCityActivity extends BaseActivity {

    public static final String CURRENT_CITY = "currentCity";
    public static final String CITY = "city";

    public static int REQ_CODE = 100;
    public static int RESULT_CODE = 101;

    private ListView mLvCity;
    private QuickIndexBar mBar;
    private TextView mTvLetter;

    private List<City> mCityList = CityDao.getCityList();

    private Handler mHandler = new Handler();
    private TextView mTvCurrentCity;

    private String mCurrentCity;
    private ImageView mIvBack;
    private TextView mTvSearchCity;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_choose_city;
    }

    @Override
    protected void initView() {
        mLvCity = findViewById(R.id.lv_city);
        mBar = findViewById(R.id.bar);
        mTvLetter = findViewById(R.id.tv_letter);
        mIvBack = findViewById(R.id.iv_back);
        mTvSearchCity = findViewById(R.id.tv_search_city);

        // 隐藏listView的滚动条
        mLvCity.setVerticalScrollBarEnabled(false);

        addCurrentCityView();
        addLatelyCityView();
    }

    @Override
    protected void initData() {
        String currentCity = getIntent().getStringExtra(CURRENT_CITY);
        if(!TextUtils.isEmpty(currentCity)){
             mTvCurrentCity.setText(currentCity);
        }
    }

    @Override
    protected void initListener() {
        mLvCity.setAdapter(new CityAdapter(mCityList));

        // 侧边栏设置点击监听
        mBar.setListener(new QuickIndexBar.OnLetterUpdateListener() {

            @Override
            public void onLetterUpdate(String letter) {
                showLetter(letter);// 在屏幕中间显示所按下的字母

                if (letter.equals("当前")) {
                    mLvCity.setSelection(0);
                    return;
                } else if (letter.equals("最近")) {
                    mLvCity.setSelection(1);
                    return;
                }
                // 根据首字母定位listView
                for (int i = 0; i < mCityList.size(); i++) {
                    City city = mCityList.get(i);
                    String l = city.getPinyin().charAt(0) + "";
                    if (l.equalsIgnoreCase(letter)) {// 匹配成功
                        mLvCity.setSelection(i);
                        break;
                    }
                }
            }
        });


        mLvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < mLvCity.getHeaderViewsCount()) {
                    return;
                }
                position = position - mLvCity.getHeaderViewsCount();
                City city = mCityList.get(position);
                saveCityToSp(city.getName()); //保存到sp中
                setResult(RESULT_CODE,new Intent().putExtra(CITY,city.getName()));
                EventBus.getDefault().post(new ChooseCityEvent(city.getName()));
                finish();
            }
        });

       mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTvSearchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseCityActivity.this, SearchCityActivity.class));
            }
        });
    }

    /**
     * 显示所触摸到的字母
     *
     * @param letter
     */
    protected void showLetter(String letter) {
        mTvLetter.setVisibility(View.VISIBLE);// 设置为可见
        mTvLetter.setText(letter);

        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mTvLetter.setVisibility(View.GONE);
            }
        }, 1000);// 两秒后隐藏
    }

    /**
     * 添加当前城市
     */
    private void addCurrentCityView() {
        View currentCityView = View.inflate(UIUtils.getContext(), R.layout.item_current_city, null);
        mTvCurrentCity = (TextView) currentCityView.findViewById(R.id.tv_current_city);

        mTvCurrentCity.setText(getString(R.string.unkown));

        mTvCurrentCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentCity = mTvCurrentCity.getText().toString();
                if (!TextUtils.isEmpty(mCurrentCity) && !mCurrentCity.equals("未知")) {
                    saveCityToSp(mCurrentCity); //保存到sp中
                    setResult(RESULT_CODE,new Intent().putExtra(CITY,mCurrentCity));
                    EventBus.getDefault().post(new ChooseCityEvent(mCurrentCity));
                    finish();
                }
            }
        });

        mLvCity.addHeaderView(currentCityView);
    }

    /**
     * 添加历史访问城市
     */
    private void addLatelyCityView() {
        //读取sp中保存的历史访问的城市json
        String historyCityJson = PreUtils.getString(Constants.HISTORY_CITY, "");

        if (TextUtils.isEmpty(historyCityJson)) {
            //如果没有保存历史访问的城市记录，则不用添加历史访问这个view
            return;
        } else {
            //有城市 [{"district":"广州"},{"district":"深圳"},{"district":"揭阳"}]
            View latelyCityView = View.inflate(UIUtils.getContext(), R.layout.item_lately_city, null);

            LinearLayout llLatelyCityRoot = (LinearLayout) latelyCityView.findViewById(R.id.ll_lately_city_root);
            LinearLayout llLatelyCity = (LinearLayout) latelyCityView.findViewById(R.id.ll_lately_city);

            llLatelyCityRoot.setVisibility(View.VISIBLE);
            llLatelyCity.setVisibility(View.VISIBLE);

            JSONArray jsonArray = JSONArray.parseArray(historyCityJson);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                final String city = jsonObject.getString("district");
                TextView tvCity = (TextView) llLatelyCity.getChildAt(i);
                tvCity.setText(city);
                tvCity.setVisibility(View.VISIBLE);
                tvCity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveCityToSp(city); //保存到sp中
                        setResult(RESULT_CODE,new Intent().putExtra(CITY,city));
                        EventBus.getDefault().post(new ChooseCityEvent(city));
                        finish();
                    }
                });
            }

            mLvCity.addHeaderView(latelyCityView);
        }

    }

}
