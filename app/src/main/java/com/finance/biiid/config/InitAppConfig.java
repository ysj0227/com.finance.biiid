package com.finance.biiid.config;

import android.content.Context;

import com.finance.commonlib.base.BaseConfig;

public class InitAppConfig extends BaseConfig {

    public static String APP_URL = "";


    @Override
    protected void initTest(Context context, String env) {
        APP_URL = AppConfig.URL_TEST;
//        APP_URL = AppConfig.URL_RELEASE;
    }

    @Override
    protected void initRelease(Context context, String env) {
        APP_URL = AppConfig.URL_RELEASE;
    }
}
