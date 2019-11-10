package com.finance.biiid.config;

import com.finance.commonlib.base.BaseApplication;
import com.finance.commonlib.utils.CommonHelper;

public class AppConfig {
    /**
     * 调取相机的fileprovider
     */
    public static final String FILE_PROVIDER_AUTHORITY =
            CommonHelper.getAppPackageName(BaseApplication.getContext()) + ".fileprovider";

    /**
     * 测试地址
     */
    public final static String URL_TEST = "https://pai.qianyusoft.cn/ddq_front/entrance.html";
    /**
     * 线上地址
     */
    public final static String URL_RELEASE = "https://wei.bidddq.com/ddq_front/entrance.html";

    /**
     * 微信分享登录
     */
    public final static String WX_TYPE = "WX_TYPE_DDQ";
    public final static int WX_TYPE_FRIEND=0;
    public final static int WX_TYPE_TIMELINE=1;
    public final static int WX_TYPE_AUTH=2;

}
