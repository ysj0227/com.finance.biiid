package com.finance.biiid.config;

import android.content.Context;

import com.finance.commonlib.base.BaseConfig;

public class InitAppConfig extends BaseConfig {

    public static String APP_URL = "";
    //查询支付订单
    public static String QUERY_TRADE_STATUS = "";


    @Override
    protected void initTest(Context context, String env) {
//        APP_URL = "https://pai.qianyusoft.cn/ddq_front/entrance.html";
        APP_URL = "https://pai.qianyusoft.cn/ddq_app/entrance.html";
        QUERY_TRADE_STATUS = "https://pai.qianyusoft.cn/ddq/appQueryWxOrder";
    }

    @Override
    protected void initRelease(Context context, String env) {
//        APP_URL = "https://wei.bidddq.com/ddq_front/entrance.html";
        APP_URL = "https://wei.bidddq.com/ddq_app/entrance.html";
        QUERY_TRADE_STATUS = "https://wei.bidddq.com/ddq/appQueryWxOrder";
    }
}
