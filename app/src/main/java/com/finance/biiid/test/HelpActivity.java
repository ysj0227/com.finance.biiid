package com.finance.biiid.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.finance.biiid.R;
import com.finance.biiid.webview.SMWebChromeClient;
import com.finance.biiid.webview.SMWebViewClient;
import com.finance.commonlib.base.BaseActivity;
import com.finance.commonlib.utils.StatusBarUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


/**
 * 用户协议，隐私
 *
 * @author yangshijie
 */
@EActivity(R.layout.activity_help)
public class HelpActivity extends BaseActivity implements View.OnClickListener {

    public final static String HELP = "https://support.qq.com/product/42486";
    /**
     * 用户的头像url
     */
    private static final String AVATAR_URL = "http://test.cdn.sunmi.com/IMG/uploadicon/default/1542875988_thumb.jpg";
    @ViewById(R.id.wv_help)
    WebView webView;
    SMWebChromeClient webChrome;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        loadWebView(HELP);
    }

    @Override
    public void onClick(View v) {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView(String url) {//绑定用户，post参数
        String postData = String.format("nickname=%s&avatar=%s&openid=%s", "me", AVATAR_URL, "42684");

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.postUrl(url, postData.getBytes());

        // 设置标题
        webChrome = new SMWebChromeClient(this);
        // 设置setWebChromeClient对象
        webView.setWebChromeClient(webChrome);
        // 可以运行JavaScript
        WebSettings webSetting = webView.getSettings();
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowFileAccess(true);
        //不用启动客户端的浏览器来加载未加载出来的数据
        webView.setWebViewClient(new SMWebViewClient(this) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!TextUtils.isEmpty(url) && url.startsWith("weixin://")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
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
            }

            @Override
            protected void receiverError(WebView view, WebResourceRequest request, WebResourceError error) {
                hideLoadingDialog();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && webChrome != null) {
            if (requestCode == SMWebChromeClient.CHOOSE_REQUEST_CODE) {
                webChrome.uploadImage(data, resultCode);
            }
        } else if (resultCode == Activity.RESULT_CANCELED && webChrome != null) {
            webChrome.cancelCallback();
        }
    }

}
