package com.finance.biiid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSON;
import com.finance.biiid.config.AppConfig;
import com.finance.biiid.notifications.CommonNotifications;
import com.finance.biiid.utils.BitmapUtils;
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
import java.util.HashMap;
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

    private Uri imageUri;
    private String url = AppConfig.URL_TEST;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this);
        rlHeight.getLayoutParams().height = CommonHelper.statusHeight(this) +
                CommonHelper.dp2px(this, 50);
        rlHeight.setPadding(0, CommonHelper.statusHeight(this), 0, 0);
        loadWebView(url);
        // 设置setWebChromeClient对象
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                tvTitle.setText(title);
            }
        });
    }

    @Click(R.id.iv_back)
    void backClick() {
        onBackPressed();
    }

    @Click(R.id.iv_more)
    void moreClick() {
        //TODO
        // 分享的固定内容
        showDialog(context, TYPE_SHARE, "");
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
            webView.loadUrl(url);//后面不带 '/' 打不开
        });
    }

    //Android to js
    @UiThread
    void postBase64String(Bitmap bitmap) {
        String str = BitmapUtils.convertIconToString(bitmap);
        Map<String, String> map = new HashMap<>();
        map.put("data", str);
        webView.loadUrl("javascript:compressedImageData" + "('" + JSON.toJSONString(map) + "')");
    }

    //js传递给Android
    private class JsInterface {
        private Context mContext;

        public JsInterface(Context context) {
            this.mContext = context;
        }

        @JavascriptInterface
        public void wechatShare(String data) {
            Log.e("tag ", "js to android wechatShare=" + data);
            showDialog(MainActivity.this, TYPE_SHARE, data);
        }

        @JavascriptInterface
        public void wechatPay(String data) {
            Log.e("tag ", "js to android wechatPay=" + data);
            gotoWxPayActivity();
        }

        @JavascriptInterface
        public void getPicture() {
            Log.e("tag ", "js to android getPicture");
            showDialog(MainActivity.this, TYPE_CANER, "");
        }

        @JavascriptInterface
        public void wechatLogin() {
            Log.e("tag ", "js to android wechatLogin");
            gotoWxAuthActivity();
        }

        @JavascriptInterface
        public void checkWechatLoginStatus(String data) {
            Log.e("tag ", "js to android checkWechatLoginStatus");
            gotoWxAuthActivity();
        }
    }

    //微信授权登录
    private void gotoWxAuthActivity() {
        Intent intent = new Intent(MainActivity.this, WXEntryActivity.class);
        intent.putExtra(AppConfig.WX_TYPE, AppConfig.WX_TYPE_AUTH);
        startActivity(intent);
    }

    //分享
    private void gotoWxActivity(int type, String data) {
        Intent intent = new Intent(MainActivity.this, WXEntryActivity.class);
        intent.putExtra(AppConfig.WX_TYPE, type);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    //跳转微信支付
    private void gotoWxPayActivity() {
        //是否支持微信支付
        boolean isPaySupported = MyApplication.WXapi.getWXAppSupportAPI() >= com.tencent.mm.opensdk.constants.Build.PAY_SUPPORTED_SDK_INT;
        if (isPaySupported) {
            Intent intent = new Intent(MainActivity.this, WXPayEntryActivity.class);
            startActivity(intent);
        } else {
            shortTip(R.string.wx_str_no_support_pay);
        }
    }

    private Dialog dialog;

    public void showDialog(final Context getActivity, int type, String data) {
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
            textUp.setText("分享到微信好友");
            textDown.setText("分享到朋友圈");
        } else if (type == TYPE_CANER) {
            textUp.setText("拍照");
            textDown.setText("从相册选择");
        }
        //on click
        View.OnClickListener listener = v -> {
            int i = v.getId();
            if (type == TYPE_SHARE) {
                dialog.dismiss();
                if (i == R.id.tvUp) {
                    gotoWxActivity(AppConfig.WX_TYPE_FRIEND, data);
                } else if (i == R.id.tvDown) {
                    gotoWxActivity(AppConfig.WX_TYPE_TIMELINE, data);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                handleImageOnKitKat(data);
            }
        }
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
            openAlbum();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
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
            Bitmap bitmap = BitmapFactory.decodeFile(imagesPath);
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
        return new int[]{CommonNotifications.weChatData};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        if (args == null) {
            return;
        }
        if (id == CommonNotifications.weChatData) {
            String resp= (String) args[0];
            Log.e("tag", "1111 weChatData= " + resp);
        }
    }
}
