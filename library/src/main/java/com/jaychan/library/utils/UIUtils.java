package com.jaychan.library.utils;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import com.jaychan.library.app.MyApplication;

/**
 * @author JayChan
 * @date 2018/12/13  15:14
 */
public class UIUtils {

    public static Toast mToast;

    public static void showToast(int msgId) {
        showToast(getString(msgId), Toast.LENGTH_SHORT);
    }

    public static void showToast(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(String msg, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(getContext(), "", duration);
        }
        mToast.setText(msg);
        mToast.show();
    }

    /**
     * 得到上下文
     *
     * @return
     */
    public static Context getContext() {
        return MyApplication.mContext;
    }

    /**
     * 得到resources对象
     *
     * @return
     */
    public static Resources getResource() {
        return getContext().getResources();
    }

    /**
     * 得到string.xml中的字符串
     *
     * @param resId
     * @return
     */
    public static String getString(int resId) {
        return getResource().getString(resId);
    }

}
