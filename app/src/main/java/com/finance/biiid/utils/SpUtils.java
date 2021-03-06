package com.finance.biiid.utils;

import android.content.SharedPreferences;

import com.finance.commonlib.base.BaseApplication;
import com.finance.commonlib.utils.SharedManager;


public class SpUtils {

    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String UNIONID = "UNIONID";
    private static final String AGREE_PROTOCOL = "AGREE_PROTOCOL";

    SpUtils() {
    }

    private static SharedPreferences getSharedPreference() {
        return SharedManager.getSharedPreference(BaseApplication.getContext());
    }

    public static void saveWXRefreshToken(String refreshToken) {
        SharedManager.putValue(BaseApplication.getContext(), REFRESH_TOKEN, refreshToken);
    }

    public static String getWXRefreshToken() {
        return SharedManager.getValue(BaseApplication.getContext(), REFRESH_TOKEN);
    }

    public static void saveWXUnionid(String unionid) {
        SharedManager.putValue(BaseApplication.getContext(), UNIONID, unionid);
    }

    public static String getWXUnionid() {
        return SharedManager.getValue(BaseApplication.getContext(), UNIONID);
    }

    public static void saveProtocol() {
        SharedManager.putValue(BaseApplication.getContext(), AGREE_PROTOCOL, "Y");
    }

    public static String getProtocol() {
        return SharedManager.getValue(BaseApplication.getContext(), AGREE_PROTOCOL);
    }

}
