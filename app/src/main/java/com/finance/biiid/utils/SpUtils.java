package com.finance.biiid.utils;

import android.content.SharedPreferences;

import com.finance.commonlib.base.BaseApplication;
import com.finance.commonlib.utils.SharedManager;


public class SpUtils {

    private static final String UDP_ROUTER = "UDP_ROUTER";

    SpUtils() {
    }

    private static SharedPreferences getSharedPreference() {
        return SharedManager.getSharedPreference(BaseApplication.getContext());
    }

    public static void saveUDPName(String name) {
        SharedManager.putValue(BaseApplication.getContext(), UDP_ROUTER, name);
    }

    public static void clearUDPName() {
        SharedManager.clearValue(BaseApplication.getContext(), UDP_ROUTER);
    }

    public static String getUDPName() {
        return SharedManager.getValue(BaseApplication.getContext(), UDP_ROUTER);
    }


}
