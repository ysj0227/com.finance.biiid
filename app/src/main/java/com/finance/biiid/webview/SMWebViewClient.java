package com.finance.biiid.webview;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Message;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public abstract class SMWebViewClient extends WebViewClient {

    public Activity mContext;

    public SMWebViewClient(Activity activity) {
        super();
        this.mContext = activity;
    }

    /**
     * (1) 当请求的方式是"POST"方式时这个回调是不会通知的。
     * (2) 当我们访问的地址需要我们应用程序自己处理的时候，可以在这里截获，比如我们发现跳转到的是一个market的链接，那么我们可以直接跳转到应用市场，或者其他app。
     *
     * @param view 接收WebViewClient的那个实例，前面看到webView.setWebViewClient(new MyAndroidWebViewClient())，即是这个webview。
     * @param url  即将要被加载的url
     * @return true 当前应用程序要自己处理这个url， 返回false则不处理。
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //默认用true,防止ERR_UNKNOW_URL_SCHEME错误
        return true;
    }

    @Override
    public void onReceivedSslError(WebView view, final SslErrorHandler handler,
                                   SslError error) {
        handler.proceed();
    }

    /**
     * goBack时重新发送POST数据
     */
    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        resend.sendToTarget();
    }

    /**
     * 加载异常
     */
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        receiverError(view, request, error);
    }

    protected abstract void receiverError(WebView view, WebResourceRequest request, WebResourceError error);

}