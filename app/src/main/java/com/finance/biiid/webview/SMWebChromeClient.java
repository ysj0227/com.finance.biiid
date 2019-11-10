package com.finance.biiid.webview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.finance.biiid.R;


/**
 * Class  Name: SMWebChromeClient
 * Description: 自定义实现webView选择本地图片
 * Created by Bruce on 18/11/27
 */
public class SMWebChromeClient extends WebChromeClient {
    public static final int CHOOSE_REQUEST_CODE = 10001;
    private ValueCallback<Uri> filePathCallback;
    private ValueCallback<Uri[]> filePathCallbacks;
    private Activity mActivity;

    private boolean mIsInjectedJS;
    private Callback callback;
    private JsCallJava mJsCallJava;

    public SMWebChromeClient(Activity activity) {
        mActivity = activity;
    }

    public SMWebChromeClient(Activity activity, String injectedName, Class injectedCls) {
        this.mActivity = activity;
        mJsCallJava = new JsCallJava(injectedName, injectedCls);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (callback != null) {
            callback.onReceivedTitle(title);
        }
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (callback != null) {
            callback.onProgressChanged(newProgress);
            if (newProgress == 100) {
                callback.onProgressComplete();
            }
        }

        //为什么要在这里注入JS
        //1 OnPageStarted中注入有可能全局注入不成功，导致页面脚本上所有接口任何时候都不可用
        //2 OnPageFinished中注入，虽然最后都会全局注入成功，但是完成时间有可能太晚，当页面在初始化调用接口函数时会等待时间过长
        //3 在进度变化时注入，刚好可以在上面两个问题中得到一个折中处理
        //为什么是进度大于25%才进行注入，因为从测试看来只有进度大于这个数字页面才真正得到框架刷新加载，保证100%注入成功
        if (newProgress <= 25) {
            mIsInjectedJS = false;
        } else if (!mIsInjectedJS) {
            if (view instanceof SMWebView) {
                SMWebView smWebView = (SMWebView) view;
                smWebView.injectJavascriptInterfaces();
            }
            if (mJsCallJava != null) {
                view.loadUrl(mJsCallJava.getPreloadInterfaceJS());
            }
            mIsInjectedJS = true;
        }
        super.onProgressChanged(view, newProgress);
    }

    // js上传文件的<input type="file" name="fileField" id="fileField" />事件捕获
    // Android > 4.1.1 调用这个方法
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        if (filePathCallback != null) {
            return;
        }
        filePathCallback = uploadMsg;
        selectImage();
    }

    // 3.0 + 调用这个方法
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        filePathCallback = uploadMsg;
        selectImage();
    }

    // Android < 3.0 调用这个方法
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        filePathCallback = uploadMsg;
        selectImage();
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        filePathCallbacks = filePathCallback;
        selectImage();
        return true;
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (mActivity != null) {
            mActivity.startActivityForResult(Intent.createChooser(intent,
                    mActivity.getString(R.string.str_title_selected_pic)), CHOOSE_REQUEST_CODE);
        }
    }

    /**
     * 所有需要上传图片的地方必须在对应的onActivityResult实现此方法
     */
    public void uploadImage(Intent data, int resultCode) {
        if (filePathCallback != null) {
            Uri result = data == null || resultCode != Activity.RESULT_OK ? null
                    : data.getData();
            if (result != null) {
                filePathCallback.onReceiveValue(result);
            } else {
                filePathCallback.onReceiveValue(null);
            }
        }
        if (filePathCallbacks != null) {
            Uri result = data == null || resultCode != Activity.RESULT_OK ? null
                    : data.getData();
            if (result != null) {
                filePathCallbacks.onReceiveValue(new Uri[]{result});
            } else {
                filePathCallbacks.onReceiveValue(null);
            }
        }

        filePathCallback = null;
        filePathCallbacks = null;
    }

    /**
     * 防止点击dialog的取消按钮之后，就不再次响应点击事件了
     */
    public void cancelCallback() {
        if (filePathCallback != null) {
            filePathCallback.onReceiveValue(null);
        }

        if (filePathCallbacks != null) {
            filePathCallbacks.onReceiveValue(null);
        }
    }

    public interface Callback {

        void onProgressChanged(int progress);

        void onProgressComplete();

        void onReceivedTitle(String title);
    }

}
