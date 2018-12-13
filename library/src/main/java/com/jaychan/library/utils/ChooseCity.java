package com.jaychan.library.utils;

import android.app.Activity;
import android.content.Intent;

import com.jaychan.library.ui.activity.ChooseCityActivity;

/**
 * @author JayChan
 * @date 2018/12/13  15:48
 */
public class ChooseCity {

    private static ChooseCity mChooseCity;
    private static Activity mActivity;
    private String mCity;

    public static ChooseCity with(Activity activity) {
        mActivity = activity;
        mChooseCity = new ChooseCity();
        return mChooseCity;
    }

    public ChooseCity currentCity(String city) {
        mCity = city;
        return mChooseCity;
    }

    public void start() {
        Intent intent = new Intent(mActivity, ChooseCityActivity.class);
        intent.putExtra(ChooseCityActivity.CURRENT_CITY, mCity);
        mActivity.startActivityForResult(intent, ChooseCityActivity.REQ_CODE);
    }
}
