package com.finance.commonlib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


public class SharedManager {

    private static final String SHARED_NAME = "cache_android_btddq"; //默认缓存文件名

    public static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 获取SharedPreference 值
     */
    public static int getIntValue(Context context, String key) {
        return getSharedPreference(context).getInt(key, -1);
    }

    /**
     * 获取SharedPreference 值
     */
    public static String getValue(Context context, String key) {
        return getSharedPreference(context).getString(key, "");
    }

    /**
     * 获取SharedPreference 值，带默认值
     */
    public static String getValue(Context context, String key, String defValue) {
        return getSharedPreference(context).getString(key, defValue);
    }

    public static boolean getBooleanValue(Context context, String key) {
        return getSharedPreference(context).getBoolean(key, false);
    }

    /**
     * 设置SharedPreference 值
     */
    public static boolean putValue(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        if (TextUtils.isEmpty(value)) {
            editor.remove(key);
        } else {
            editor.putString(key, value);
        }
        return editor.commit();
    }

    public static void putValue(Context context, String key, boolean value) {
        SharedPreferences.Editor edit = getSharedPreference(context).edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    public static void putValue(Context context, String key, int value) {
        SharedPreferences.Editor edit = getSharedPreference(context).edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public static void clearValue(Context context, String key) {
        getSharedPreference(context).edit().remove(key).apply();
    }

}

