package com.finance.commonlib.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.Random;

/**
 * Description:
 * Created by bruce on 2019/2/13.
 */
public class Utils {

    /**
     * 获取manifest文件meta值
     */
    public static String getMetaValue(Context context, String keyName, String defValue) {
        Object value = null;
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            value = applicationInfo.metaData.get(keyName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value != null ? value.toString() : defValue;
    }

    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static Random mRandom = new Random();

    public static String getMsgId() {
        if (mRandom == null) {
            mRandom = new Random();
        }
        mRandom.setSeed(System.currentTimeMillis());
        return (mRandom.nextInt(11) + 10) + String.valueOf(System.currentTimeMillis()).substring(5);
    }

}
