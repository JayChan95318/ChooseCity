package com.jaychan.library.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jaychan.library.R;
import com.jaychan.library.bean.City;
import com.jaychan.library.db.CityDao;
import com.jaychan.library.event.ChooseCityEvent;
import com.jaychan.library.ui.adapter.SearchCityReultAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JayChan
 * @date 2018/12/13  15:32
 */
public class SearchCityActivity extends BaseActivity {

    private EditText mEtSearchCity;
    private ListView mLvSearchCity;
    private LinearLayout mLlNoResult;

    private List<City> mResultList = new ArrayList<City>();
    private SearchCityReultAdapter mSearchReultAdapter;
    private ImageView mIvBack;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_search_city;
    }

    @Override
    protected void initView() {
        mEtSearchCity = findViewById(R.id.et_search_city);
        mLvSearchCity = findViewById(R.id.lv_search_city);
        mLlNoResult = findViewById(R.id.ll_no_result);
        mIvBack = findViewById(R.id.iv_back);
    }

    @Override
    protected void initListener() {
        mSearchReultAdapter = new SearchCityReultAdapter(mResultList);
        mLvSearchCity.setAdapter(mSearchReultAdapter);


        mLvSearchCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = mResultList.get(position);
                saveCityToSp(city.getName());
                setResult(ChooseCityActivity.RESULT_CODE,new Intent().putExtra(ChooseCityActivity.CITY,city.getName()));
                EventBus.getDefault().post(new ChooseCityEvent(city.getName()));
                mActivities.get(mActivities.size()-2).finish();
                finish();
            }
        });

        //设置editText文本变化的监听
        mEtSearchCity.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String content = s.toString();
                if (TextUtils.isEmpty(content)) {
                    // 如果文本内容为空，则左侧显示搜索的图标
                    mEtSearchCity.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0, 0, 0);

                    mResultList.clear();//清空集合
                    mLlNoResult.setVisibility(View.GONE);//隐藏找不到结果

                    mSearchReultAdapter.notifyDataSetChanged();
                } else {
                    // 如果文本内容不为空，则右侧的删除文本的图标显示
                    mEtSearchCity.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0,
                            R.mipmap.et_delete, 0);

                    searchCity(content);//搜索城市
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        // 为EditText设置右侧图片的点击事件
        mEtSearchCity.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEtSearchCity.getCompoundDrawables();
                // 得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = mEtSearchCity.getCompoundDrawables()[2];
                // 如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                // 如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;

                if (event.getX() > mEtSearchCity.getWidth() - mEtSearchCity.getPaddingRight()
                        - drawable.getIntrinsicWidth()) {
                    // 右侧图片点击清除文本
                    mEtSearchCity.setText("");
                    mResultList.clear();
                    mSearchReultAdapter.notifyChange(mResultList);
                }

                return false;
            }
        });

        mEtSearchCity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    //软键盘上的搜索键
                    if (mResultList.size() == 1) {
                        String city = mResultList.get(0).getName();
                        saveCityToSp(city);//保存到sp中
                        setResult(ChooseCityActivity.RESULT_CODE,new Intent().putExtra(ChooseCityActivity.CITY,city));
                        EventBus.getDefault().post(new ChooseCityEvent(city));
                        mActivities.get(mActivities.size()-2).finish();
                        finish();
                        return true;
                    }
                }
                return false;
            }
        });

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 搜索城市
     */
    private void searchCity(String key) {
        mResultList = CityDao.searchCity(key);
        if (mResultList.size() == 0) {
            //查找不到结果
            mLlNoResult.setVisibility(View.VISIBLE);//显示找不到结果的布局
            mLvSearchCity.setVisibility(View.GONE);
        } else {
            //有结果
            mLlNoResult.setVisibility(View.GONE);//隐藏查找不到结果的布局
            mLvSearchCity.setVisibility(View.VISIBLE);
            mSearchReultAdapter.notifyChange(mResultList);
        }
    }
}
