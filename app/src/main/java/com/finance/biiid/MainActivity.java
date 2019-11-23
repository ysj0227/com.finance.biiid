package com.finance.biiid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.BuildCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.donkingliang.imageselector.utils.ImageUtil;
import com.donkingliang.imageselector.utils.UriUtils;
import com.finance.biiid.config.AppConfig;
import com.finance.biiid.config.InitAppConfig;
import com.finance.biiid.config.WxShareConfig;
import com.finance.biiid.notifications.CommonNotifications;
import com.finance.biiid.previewimg.ImageBigActivity_;
import com.finance.biiid.utils.BitmapUtils;
import com.finance.biiid.utils.SpUtils;
import com.finance.biiid.webview.SMWebViewClient;
import com.finance.biiid.wxapi.WXEntryActivity;
import com.finance.biiid.wxapi.WXPayEntryActivity;
import com.finance.commonlib.base.BaseActivity;
import com.finance.commonlib.utils.CommonHelper;
import com.finance.commonlib.utils.NetworkUtils;
import com.finance.commonlib.utils.StatusBarUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("Registered")
@EActivity(R.layout.activity_webview)
public class MainActivity extends BaseActivity {
    private final static int TYPE_SHARE = 100;
    private final static int TYPE_CANER = 101;
    private final static int TAKE_PHOTO = 102;
    private final static int CHOOSE_PHOTO = 103;
    private final static int PERMISSION_CAMERA = 104;
    private final static int PERMISSION_ALBUM = 105;
    private final static String PACKAGENAME_FILEPROVIDER = "com.finance.biiid.fileprovider";
    @ViewById(R.id.wv_view)
    WebView webView;
    @ViewById(R.id.tv_title)
    TextView tvTitle;
    @ViewById(R.id.v_line)
    View vLine;
    @ViewById(R.id.rl_title)
    RelativeLayout rlHeight;
    @ViewById(R.id.rl_exception)
    RelativeLayout rlException;
    @ViewById(R.id.btn_again)
    Button btnAgain;
    @ViewById(R.id.iv_more)
    ImageView ivMore;

    private Uri imageUri;
    private int mWXType;
    private String webViewUrl;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this);
        rlHeight.getLayoutParams().height = CommonHelper.statusHeight(this) +
                CommonHelper.dp2px(this, 50);
        rlHeight.setPadding(0, CommonHelper.statusHeight(this), 0, 0);
        loadWebView(InitAppConfig.APP_URL);
        // 设置setWebChromeClient对象
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (WxShareConfig.isHideShare(title)) {
                    ivMore.setVisibility(View.GONE);
                } else {
                    ivMore.setVisibility(View.VISIBLE);
                }
                if (TextUtils.isEmpty(title) || title.contains("http")) {
                    tvTitle.setText(R.string.app_name);
                } else {
                    tvTitle.setText(title);
                }
            }
        });
    }

    @Click(R.id.iv_back)
    void backClick() {
        onBackPressed();
    }

    @Click(R.id.iv_more)
    void moreClick() {
        showDialog(context, TYPE_SHARE);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView(String url) {

        final WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowUniversalAccessFromFileURLs(true);
        webSetting.setDefaultTextEncodingName("utf-8");
        webSetting.setSupportZoom(false);
        webSetting.setDomStorageEnabled(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadsImagesAutomatically(true);
        webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        webSetting.setBlockNetworkImage(true);//同步请求图片
        webSetting.setAllowFileAccess(true);// 设置允许访问文件数据
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setBlockNetworkImage(false);//解决图片不显示
        webView.addJavascriptInterface(new JsInterface(MainActivity.this), "android");
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        webView.setWebChromeClient(new WebChromeClient());//
        webView.loadUrl(url);
        webView.setWebViewClient(new SMWebViewClient(this) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
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
                Log.d(TAG, "webview onPageFinished url=" + url);
                webViewUrl = url;
                hideLoadingDialog();
            }

            @Override
            protected void receiverError(WebView view, WebResourceRequest request, WebResourceError error) {
                hideLoadingDialog();
                receiverExceptionError(view);
            }
        });
    }

    /**
     * 网络异常
     *
     * @param view
     */
    private void receiverExceptionError(WebView view) {
        webView.setVisibility(View.GONE);
        vLine.setVisibility(View.GONE);
        rlException.setVisibility(View.VISIBLE);
        btnAgain.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(context)) {
                shortTip(getString(R.string.str_check_net));
                return;
            }
            rlException.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            vLine.setVisibility(View.VISIBLE);
            view.clearCache(true);
            view.clearHistory();
//            if (TextUtils.isEmpty(webViewUrl)) {
//                webView.loadUrl(InitAppConfig.APP_URL);
//            } else {
//                webView.loadUrl(webViewUrl);
//            }
        });
    }

    /*************Android to js********
     /**
     * 上传图片到js
     * @param bitmap bitmap
     */
    @UiThread
    void postBase64String(Bitmap bitmap) {
        String str = BitmapUtils.convertIconToString(bitmap);
        Map<String, String> map = new HashMap<>();
        map.put("data", str);
        webView.loadUrl("javascript:compressedImageData" + "('" + JSON.toJSONString(map) + "')");
    }

    /**
     * 上传微信授权信息到js
     *
     * @param d1 d1
     * @param d2 d2
     */
    @UiThread
    void wxChatAuthSuccess(String d1, String d2) {
        try {
            JSONObject object = new JSONObject();
            JSONObject object1 = JSONObject.parseObject(d1);
            JSONObject object2 = JSONObject.parseObject(d2);
            object.putAll(object1);
            object.putAll(object2);
            webView.loadUrl("javascript:getUserLoginInfo" + "('" + object.toString() + "')");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @UiThread
    void checkWeChatLoginStatus(int type) {
        try {
            JSONObject object = new JSONObject();
            object.put("refresh_token", SpUtils.getWXRefreshToken());
            object.put("unionid", SpUtils.getWXUnionid());
            object.put("type", type);
            webView.loadUrl("javascript:checkWeChatLoginStatus" + "('" + object.toString() + "')");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 支付完成后调用js显示支付状态
     *
     * @param code
     * @param url
     */
    @UiThread
    void appQueryWxOrderReturn(int code, String url) {
        try {
            JSONObject object = new JSONObject();
            object.put("code", code);
            object.put("url", url);
            webView.loadUrl("javascript:appQueryWxOrderReturn" + "('" + object.toString() + "')");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * android to js 获取分享内容
     */
    @UiThread
    void wechatShare() {
        webView.loadUrl("javascript:wechatShare()");
    }

    //js传递给Android
    private class JsInterface {
        private Context mContext;

        public JsInterface(Context context) {
            this.mContext = context;
        }

        @JavascriptInterface
        public void wechatShare(String data) {
            Log.d("tag ", "js to android wechatShare=" + data);
            gotoWxActivity(mWXType, data);
        }

        @JavascriptInterface
        public void wechatPay(String data) {
            Log.d("tag ", "js to android wechatPay=" + data);
            if (TextUtils.isEmpty(data)) {
                shortTip(R.string.pay_fail);
                return;
            }
            gotoWxPayActivity(data);
        }

        @JavascriptInterface
        public void getPicture() {
            Log.d("tag ", "js to android getPicture");
            showDialog(MainActivity.this, TYPE_CANER);
        }

        @JavascriptInterface
        public void wechatLogin(String data) {
            Log.d("tag ", "js to android wechatLogin" + data);
            gotoWxActivity(AppConfig.WX_TYPE_AUTH, data);
        }

        @JavascriptInterface
        public void previewImage(String data) {
            Log.d("tag ", "js to android previewImage" + data);
            gotoPreviewImage(data);
        }

        @JavascriptInterface
        public void getRefreshToken(String data) {
            Log.d("tag ", "js to android getRefreshToken type=" + data);
            JSONObject object = JSONObject.parseObject(data);
            int type = object.getInteger("type");
            checkWeChatLoginStatus(type);
        }
    }

    /**
     * 预览图片
     *
     * @param data data
     */
    private void gotoPreviewImage(String data) {
        if (TextUtils.isEmpty(data)) {
            return;
        }
        JSONObject object = JSONObject.parseObject(data);
        int currentPosition = object.getIntValue("currentPosition");
        JSONArray array = object.getJSONArray("imagesUrl");
        ArrayList<String> imagesList = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            imagesList.add(array.get(i).toString());
        }
        ImageBigActivity_.intent(this)
                .imagesUrl(imagesList)
                .current(currentPosition)
                .start();
    }

    /**
     * 是否安装微信
     *
     * @return
     */
    private boolean isInstallWechat() {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    //分享 授权登录
    private void gotoWxActivity(int type, String data) {
        if (!isInstallWechat()) {
            shortTip(R.string.str_need_install_wx);
            return;
        }
        Intent intent = new Intent(MainActivity.this, WXEntryActivity.class);
        intent.putExtra(AppConfig.WX_TYPE, type);
        intent.putExtra(AppConfig.WX_DATA, data);
        startActivity(intent);
    }

    //跳转微信支付
    private void gotoWxPayActivity(String data) {
        if (!isInstallWechat()) {
            shortTip(R.string.str_need_install_wx);
            return;
        }
        //是否支持微信支付
        boolean isPaySupported = MyApplication.WXapi.getWXAppSupportAPI() >= com.tencent.mm.opensdk.constants.Build.PAY_SUPPORTED_SDK_INT;
        if (isPaySupported) {
            Intent intent = new Intent(MainActivity.this, WXPayEntryActivity.class);
            intent.putExtra(AppConfig.WX_DATA, data);
            startActivity(intent);
        } else {
            shortTip(R.string.wx_str_no_support_pay);
        }
    }

    private Dialog dialog;

    public void showDialog(final Context getActivity, int type) {
        dialog = new Dialog(getActivity, R.style.BottomDialog);
        View inflate = LayoutInflater.from(getActivity).inflate(R.layout.dialog_ddq, null);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        if (dialogWindow == null) {
            return;
        }
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        // 屏幕宽度（像素）
        WindowManager wm = (WindowManager) getActivity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        //int height = dm.heightPixels;
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = width;
        dialogWindow.setAttributes(lp);

        TextView textUp = inflate.findViewById(R.id.tvUp);
        TextView textDown = inflate.findViewById(R.id.tvDown);
        TextView textCancel = inflate.findViewById(R.id.tvCancel);
        if (type == TYPE_SHARE) {
            textUp.setText(getString(R.string.wx_share_session));
            textDown.setText(getString(R.string.wx_share_timeline));
        } else if (type == TYPE_CANER) {
            textUp.setText(getString(R.string.str_take_photo));
            textDown.setText(getString(R.string.str_select_album));
        }
        //on click
        View.OnClickListener listener = v -> {
            int i = v.getId();
            if (type == TYPE_SHARE) {
                dialog.dismiss();
                if (i == R.id.tvUp) {
                    mWXType = AppConfig.WX_TYPE_FRIEND;
                    wechatShare();
                } else if (i == R.id.tvDown) {
                    if (MyApplication.WXapi.getWXAppSupportAPI() >= com.tencent.mm.opensdk.constants.Build.TIMELINE_SUPPORTED_SDK_INT) {
                        mWXType = AppConfig.WX_TYPE_TIMELINE;
                        wechatShare();
                    } else {
                        shortTip(R.string.wx_version_no_support_timeline);
                    }
                }
            } else if (type == TYPE_CANER) {
                if (i == R.id.tvUp) {
                    openCamera();
                } else if (i == R.id.tvDown) {
                    getPhotoFromAlbum();
                }
            }
            if (i == R.id.tvCancel) {
                dialog.dismiss();
            }
        };
        textUp.setOnClickListener(listener);
        textDown.setOnClickListener(listener);
        textCancel.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.show();//显示对话框
    }

    @UiThread
    void takePhoto() {
        //创建File对象，用于储存拍照后的图片
        File outputImage = new File(getExternalCacheDir(), "biiid_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(MainActivity.this,
                    PACKAGENAME_FILEPROVIDER, outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        } else {
            takePhoto();
        }
    }

    private void getPhotoFromAlbum() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_ALBUM);
        } else {
            openAlbum(3);//TODO
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_ALBUM:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum(3);//TODO
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PHOTO) {  //将拍摄的照片显示出来
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    postBase64String(bitmap);
                    Toast.makeText(this, R.string.str_img_upload_success, Toast.LENGTH_SHORT).show();
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, R.string.str_img_upload_fail, Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == CHOOSE_PHOTO) {
//                //单选相册回调
//                handleImageOnKitKat(data);

                //获取选择器返回的数据
                ArrayList<String> images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT);
                if (BuildCompat.isAtLeastQ()) {
                    for (int i = 0; i < images.size(); i++) {
                        displayImage(images.get(i));
                    }
                } else {
                    for (int i = 0; i < images.size(); i++) {
                        displayImage(images.get(i));
                    }
                }
            }
        }
    }
//    //单选相册
//    private void openAlbum() {
//        Intent intent = new Intent("android.intent.action.GET_CONTENT");
//        intent.setType("image/*");
//        startActivityForResult(intent, CHOOSE_PHOTO);
//    }

    /**
     * 限数量的多选(比如最多9张)三方库
     */
    private void openAlbum(int hasNum) {
        ImageSelector.builder()
                .useCamera(false) // 设置是否使用拍照
                .setSingle(false)  //设置是否单选
                .setMaxSelectCount(9 - hasNum) // 图片的最大选择数量，小于等于0时，不限数量。
                .canPreview(true) //是否可以预览图片，默认为true
                .start(this, CHOOSE_PHOTO); // 打开相册
    }

    private void handleImageOnKitKat(Intent data) {
        String imagesPath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagesPath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagesPath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagesPath = uri.getPath();
        }
        displayImage(imagesPath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagesPath) {
        if (imagesPath != null) {
            Bitmap bitmap;
            if (BuildCompat.isAtLeastQ()) {
                Uri uri = UriUtils.getImageContentUri(this, imagesPath);
                bitmap = ImageUtil.getBitmapFromUri(this, uri);
            } else {
                bitmap = BitmapFactory.decodeFile(imagesPath);
            }
            postBase64String(bitmap);
            Toast.makeText(this, R.string.str_img_upload_success, Toast.LENGTH_SHORT).show();
            if (dialog != null) {
                dialog.dismiss();
            }
        } else {
            Toast.makeText(this, R.string.str_img_upload_fail, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{CommonNotifications.weChatData, CommonNotifications.weChatPayStatus};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        if (args == null) {
            return;
        }
        if (id == CommonNotifications.weChatData) {
            String d1 = (String) args[0];
            String d2 = (String) args[1];
            wxChatAuthSuccess(d1, d2);
        } else if (id == CommonNotifications.weChatPayStatus) {
            int code = (int) args[0];
            String url = (String) args[1];
            appQueryWxOrderReturn(code, url);
        }
    }

    private void clearCache() {
        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFormData();
        CookieSyncManager.createInstance(context.getApplicationContext());
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookies(null);
            cookieManager.removeAllCookie();
            cookieManager.flush();
        } else {
            cookieManager.removeSessionCookies(null);
            cookieManager.removeAllCookie();
            CookieSyncManager.getInstance().sync();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearCache();
    }
}
