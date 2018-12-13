package com.jaychan.library.ui.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jaychan.library.constants.Constants;
import com.jaychan.library.listener.PermissionListener;
import com.jaychan.library.utils.PreUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author JayChan
 * @date 2018/12/13  14:44
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final int GRANT_REQ_CODE = 0x123;

    public List<Activity> mActivities = new LinkedList<>();

    private PermissionListener permissionListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        //初始化的时候将其添加到集合中
        synchronized (mActivities) {
            mActivities.add(this);
        }

        initView();
        initData();
        initListener();
    }

    protected abstract int getLayoutResId();

    protected void initView() {
    }

    protected void initData() {
    }

    protected void initListener() {

    }

    public boolean isEventBusRegisted(Object subscribe) {
        return EventBus.getDefault().isRegistered(subscribe);
    }

    public void registerEventBus(Object subscribe) {
        if (!isEventBusRegisted(subscribe)) {
            EventBus.getDefault().register(subscribe);
        }
    }

    public void unregisterEventBus(Object subscribe) {
        if (isEventBusRegisted(subscribe)) {
            EventBus.getDefault().unregister(subscribe);
        }
    }

    /**
     * 申请运行时权限
     */
    public void requestRuntimePermission(String[] permissions, PermissionListener listener) {
        permissionListener = listener;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), GRANT_REQ_CODE);
        } else {
            if (permissionListener != null) {
                permissionListener.onGranted();
            }
        }
    }

    /**
     * 保存城市到sp中
     */
    public void saveCityToSp(String city) {
        city = city.replace("市", "");
        String historyCityJson = PreUtils.getString(Constants.HISTORY_CITY, "");
        if (TextUtils.isEmpty(historyCityJson)) {
            //如果为空
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("district", city);
            jsonArray.add(jsonObject);
            PreUtils.putString(Constants.HISTORY_CITY, jsonArray.toString());//保存到sp中
        } else {
            //如果已经保存过
            JSONArray jsonArray = JSONArray.parseArray(historyCityJson);
            if (jsonArray.size() == 3) {
                //如果已经保存三个了，则将移位，将原先的城市向右移动一位，始终只保存3个城市，将最新的放在第一个位置
                JSONObject city1Json = (JSONObject) jsonArray.get(0);
                JSONObject city2Json = (JSONObject) jsonArray.get(1);
                JSONObject city3Json = (JSONObject) jsonArray.get(2);

                String city1 = city1Json.getString("district");
                String city2 = city2Json.getString("district");

                //查找是否已经记录了
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String historyCity = jsonObject.getString("district");
                    if (city.equalsIgnoreCase(historyCity)) {
                        //如果已经有记录
                        //如果位置不是在第一位，则把它放到第一位
                        if (i != 0) {
                            city1Json.put("district", city);//替换第一位的city为选择的city
                            city2Json.put("district", city1);//第二位的city为原先第一位的city
                            if (i == 2) {
                                //如果现在选择的是和第三个一致，则将第三个移动到第一位后，其余的向右移动一位
                                city3Json.put("district", city2);
                            }
                        }

                        PreUtils.putString(Constants.HISTORY_CITY, jsonArray.toString());//保存到sp中
                        return;
                    }
                }

                city1Json.put("district", city);
                city2Json.put("district", city1);
                city3Json.put("district", city2);
            } else {
                //如果还没满3个，则往前面位置添加jsonObject
                for (int i = 0; i < jsonArray.size(); i++) {
                    //查找是否已经记录了
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String historyCity = jsonObject.getString("district");
                    if (city.equalsIgnoreCase(historyCity)) {
                        //如果已经有记录,则不用保存
                        return;
                    }
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("district", city);
                jsonArray.add(0, jsonObject);
            }

            PreUtils.putString(Constants.HISTORY_CITY, jsonArray.toString());//保存到sp中
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case GRANT_REQ_CODE:
                if (grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        if (permissionListener != null) {
                            permissionListener.onGranted();
                        }
                    } else {
                        if (permissionListener != null) {
                            permissionListener.onDenied(deniedPermissions);
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁的时候从集合中移除
        synchronized (mActivities) {
            mActivities.remove(this);
        }
    }
}
