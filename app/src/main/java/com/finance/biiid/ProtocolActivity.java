package com.finance.biiid;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.finance.biiid.webview.SMWebView;
import com.finance.biiid.webview.SMWebViewClient;
import com.finance.commonlib.base.BaseActivity;
import com.finance.commonlib.utils.StatusBarUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;


/**
 * 用户协议，隐私
 */
@EActivity(resName = "activity_protocol")
public class ProtocolActivity extends BaseActivity {

    //用户协议
    public final static String PROTOCOL_USER = "https://wei.bidddq.com/ddq_app/agreement.html";
    //隐私协议
    public final static String PROTOCOL_PRIVATE = "https://wei.bidddq.com/ddq_app/privacy.html";

    public static final int USER_PROTOCOL = 0;
    public static final int USER_PRIVATE = 1;

    @ViewById(resName = "wv_protocol")
    SMWebView webView;
    @Extra
    int protocolType;
    private CountDownTimer countDownTimer;
    private long timeout = 5000;//超时时间
    private boolean loadFail;

    @AfterViews
    protected void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        initNormal();
        // 设置标题
        WebChromeClient webChrome = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        };
        // 设置setWebChromeClient对象
        webView.setWebChromeClient(webChrome);
    }

    private void initNormal() {
        if (protocolType == USER_PROTOCOL) { //app注册协议
            loadWebView(PROTOCOL_USER);
        } else if (protocolType == USER_PRIVATE) {
            loadWebView(PROTOCOL_PRIVATE);
        }
    }

    @Click(resName = "btnImage")
    public void onClick(View v) {
        closeTimer();
        finish();
        this.overridePendingTransition(0, R.anim.activity_close_up_down);
    }

    private void startTimer() {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(timeout, timeout) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                }
            };
        }
        countDownTimer.start();
    }

    private void closeTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView(String url) {
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);
        // 可以运行JavaScript
        WebSettings webSetting = webView.getSettings();
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setJavaScriptEnabled(true);
        //自适应屏幕
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setLoadWithOverviewMode(true);
        webView.getSettings().setMixedContentMode(
                WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        // 不用启动客户端的浏览器来加载未加载出来的数据
        webView.setWebViewClient(new SMWebViewClient(this) {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                startTimer();
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoadingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoadingDialog();
                closeTimer();
            }

            @Override
            protected void receiverError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (!loadFail) {
                    loadFail = true;
                    hideLoadingDialog();
                }
            }
        });
    }
}
