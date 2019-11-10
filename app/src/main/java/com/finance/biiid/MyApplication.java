package com.finance.biiid;


import com.finance.biiid.config.Constants;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.litepal.LitePalApplication;

/**
 * Created by shiJie.yang on 2018/5/24.
 */

public class MyApplication extends LitePalApplication {
    public static IWXAPI WXapi; //第三方app和微信通信的openapi接口

    @Override
    public void onCreate() {
        super.onCreate();
        //微信分享初始化
        createWXAPI();

    }

    //初始化微信分享
    private void createWXAPI() {
        //	通过WXAPIFactory工厂，获取IWXAPI的实例
        WXapi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        WXapi.registerApp(Constants.APP_ID);
    }
}
