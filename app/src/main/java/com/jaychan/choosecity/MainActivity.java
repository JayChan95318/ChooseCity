package com.jaychan.choosecity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.jaychan.library.constants.Constants;
import com.jaychan.library.event.ChooseCityEvent;
import com.jaychan.library.listener.PermissionListener;
import com.jaychan.library.ui.activity.BaseActivity;
import com.jaychan.library.utils.ChooseCity;
import com.jaychan.library.utils.UIUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class MainActivity extends BaseActivity {

    private Button mBtnChoose;
    private TextView mTvCity;
    private LocationUtils mLocationUtils;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mBtnChoose = findViewById(R.id.btn_choose);
        mTvCity = findViewById(R.id.tv_city);
    }

    @Override
    protected void initData() {
        registerEventBus(MainActivity.this);
        mLocationUtils = new LocationUtils();
    }

    @Subscribe
    public void onChooseCityEvent(ChooseCityEvent event) {
        mTvCity.setText("当前选择的城市是 : " + event.city);
    }

    @Override
    protected void initListener() {
        mBtnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRuntimePermission(Constants.LOCATION_PERMISSIONS, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        mLocationUtils.getLonLat(MainActivity.this, new LocationUtils.LonLatListener() {
                            @Override
                            public void getLonLat(AMapLocation aMapLocation) {
                                if (aMapLocation != null) {
                                    String city = aMapLocation.getCity().replace("市", "");
                                    ChooseCity.with(MainActivity.this).currentCity(city).start();
                                }else{
                                    UIUtils.showToast(R.string.located_error);
                                    ChooseCity.with(MainActivity.this).start();
                                }
                            }
                        });
                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions) {
                        UIUtils.showToast(R.string.location_permission_denied);
                        ChooseCity.with(MainActivity.this).start();
                    }
                });
            }
        });
    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== ChooseCityActivity.RESULT_CODE){
            String city = data.getStringExtra(ChooseCityActivity.CITY);
            mTvCity.setText("当前选择的城市是 : "+city);
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterEventBus(MainActivity.this);
    }
}
