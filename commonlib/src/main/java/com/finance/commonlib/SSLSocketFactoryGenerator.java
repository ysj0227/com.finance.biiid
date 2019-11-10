package com.finance.commonlib;

import android.annotation.SuppressLint;
import android.os.Build;

import com.finance.commonlib.base.BaseApplication;
import com.finance.commonlib.utils.SocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;



public class SSLSocketFactoryGenerator {

    public SSLSocketFactory generate() {
        if (Build.VERSION.SDK_INT >= 23) {//6.0上
            return getSSLSocketFactoryUp23();
        } else {//6.0下
            return getSSLSocketFactoryDown23();
        }
    }

    /**
     * 证书校验
     */
    private SSLSocketFactory getSSLSocketFactoryUp23() {
        //信任所有证书
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};
        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());//keyManagers为空？
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return sslSocketFactory;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 证书校验
     */
    private SocketFactory getSSLSocketFactoryDown23() {
        try {
            //证书校验
            SocketFactory.SocketFactoryOptions socketFactoryOptions = new SocketFactory.SocketFactoryOptions();
            socketFactoryOptions.withCaInputStream(BaseApplication.getContext().getResources().openRawResource(R.raw.ca_cert));
            return new SocketFactory(socketFactoryOptions);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
