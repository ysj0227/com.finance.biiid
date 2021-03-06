package com.finance.biiid.config;

import android.content.Context;

import com.finance.commonlib.base.BaseConfig;

public class InitAppConfig extends BaseConfig {

    public static String APP_URL = "";
    //查询支付订单
    public static String QUERY_TRADE_STATUS = "";

    public static String APP_ENTRANCE = "";
    public static String APP_ENTRANCE1= "";
    public static String APP_INDEX_PAGE = "";
    public static String APP_CLASSIFY_PAGE = "";
    public static String APP_DISCOVER_PAGE = "";
    public static String APP_MY_PAGE = "";

    @Override
    protected void initTest(Context context, String env) {
//        APP_URL = "https://pai.qianyusoft.cn/ddq_front/entrance.html";
        APP_URL = "https://pai.qianyusoft.cn/ddq_app/entrance.html";
        QUERY_TRADE_STATUS = "https://pai.qianyusoft.cn/ddq/appQueryWxOrder";

        APP_ENTRANCE = "https://pai.qianyusoft.cn/ddq_app/entrance.html";
        APP_ENTRANCE1 = "https://pai.qianyusoft.cn/ddq_app/entrance.html#";
        APP_INDEX_PAGE = "https://pai.qianyusoft.cn/ddq_app/index.html";
        APP_CLASSIFY_PAGE = "https://pai.qianyusoft.cn/ddq_app/classify.html";
        APP_DISCOVER_PAGE = "https://pai.qianyusoft.cn/ddq_app/discover.html";
        APP_MY_PAGE = "https://pai.qianyusoft.cn/ddq_app/mypage.html";
    }

    @Override
    protected void initRelease(Context context, String env) {
//        APP_URL = "https://wei.bidddq.com/ddq_front/entrance.html";
        APP_URL = "https://wei.bidddq.com/ddq_app/entrance.html";
        QUERY_TRADE_STATUS = "https://wei.bidddq.com/ddq/appQueryWxOrder";

        APP_ENTRANCE = "https://wei.bidddq.com/ddq_app/entrance.html";
        APP_ENTRANCE1 = "https://wei.bidddq.com/ddq_app/entrance.html#";
        APP_INDEX_PAGE = "https://wei.bidddq.com/ddq_app/index.html";
        APP_CLASSIFY_PAGE = "https://wei.bidddq.com/ddq_app/classify.html";
        APP_DISCOVER_PAGE = "https://wei.bidddq.com/ddq_app/discover.html";
        APP_MY_PAGE = "https://wei.bidddq.com/ddq_app/mypage.html";
    }
}
