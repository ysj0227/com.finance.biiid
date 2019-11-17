package com.finance.biiid.config;

import android.content.Context;

import com.finance.commonlib.base.BaseConfig;

public class InitAppConfig extends BaseConfig {

    public static String APP_URL = "";

    @Override
    protected void initDev(Context context, String env) {
        APP_URL=AppConfig.URL_TEST;
    }

    @Override
    protected void initTest(Context context, String env) {
        APP_URL = AppConfig.URL_TEST;
    }

    @Override
    protected void initRelease(Context context, String env) {
        APP_URL = AppConfig.URL_RELEASE;
    }
}
