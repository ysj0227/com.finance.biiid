package com.finance.biiid;


import android.annotation.SuppressLint;

import com.finance.biiid.config.Constants;
import com.finance.biiid.config.InitAppConfig;
import com.finance.commonlib.base.BaseApplication;
import com.finance.commonlib.utils.HttpsUtils;
import com.finance.commonlib.utils.Utils;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by shiJie.yang on 2018/5/24.
 */

public class MyApplication extends BaseApplication {
    public static IWXAPI WXapi; //第三方app和微信通信的openapi接口

    @Override
    public void onCreate() {
        super.onCreate();
        String env = Utils.getMetaValue(this, "ENV_DATA", InitAppConfig.ENV_TEST);
        new InitAppConfig().init(this, env);
        //微信分享初始化
        createWXAPI();
        handleSSLHandshake();
    }

    //初始化微信分享
    private void createWXAPI() {
        //	通过WXAPIFactory工厂，获取IWXAPI的实例
        WXapi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        WXapi.registerApp(Constants.APP_ID);
    }

    /**
     * Glide加载https部分失败，设置信任证书
     */
    private void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
            SSLContext sc = SSLContext.getInstance("TLS"); // trustAllCerts信任所有的证书
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
