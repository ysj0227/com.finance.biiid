package com.finance.biiid.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.finance.biiid.MyApplication;
import com.finance.biiid.R;
import com.finance.biiid.config.AppConfig;
import com.finance.biiid.model.ShareData;
import com.finance.biiid.utils.Util;
import com.finance.biiid.utils.WxAuthLoginUtils;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    /**
     * 关于微信分享成功的回调：在这里着重说明必须使用微信提供的 WXEntryActivity类
     * 否则分享回调不成功
     */
    private final String TAG = this.getClass().getSimpleName();
    private int wxType;
    private String mData;
    private String appId = "", appSecret = "";
    private boolean isFirstOpen;
    private boolean isGoBaseResp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wx_transparent);//分享设置透明布局
        Log.d(TAG, "11111 onCreate");
        try {
            MyApplication.WXapi.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        wxType = intent.getIntExtra(AppConfig.WX_TYPE, 0);
        mData = intent.getStringExtra(AppConfig.WX_DATA);
        if (AppConfig.WX_TYPE_FRIEND == wxType || AppConfig.WX_TYPE_TIMELINE == wxType) {
            //分享
            shareWX(wxType, mData);
        } else if (AppConfig.WX_TYPE_AUTH == wxType) {
            //授权
            try {
                JSONObject object = new JSONObject(mData);
                appId = object.getString("appId");
                appSecret = object.getString("appSecret");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            auth();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "11111 onResume" + isFirstOpen + ", isGoBaseResp=" + isGoBaseResp);
        if (isFirstOpen ) {
            finish();
        }
        isFirstOpen = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        MyApplication.WXapi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        int result = 0;
        Log.d(TAG, "11111 onResp");
        isGoBaseResp = true;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (AppConfig.WX_TYPE_FRIEND == wxType || AppConfig.WX_TYPE_TIMELINE == wxType) {
                    Log.d(TAG, "11111 onResp share");
                    //分享
                    result = R.string.errcode_success;
                    Toast.makeText(WXEntryActivity.this, result, Toast.LENGTH_LONG).show();
                    this.finish();
                } else if (AppConfig.WX_TYPE_AUTH == wxType) {
                    Log.d(TAG, "11111 onResp auth");
                    //授权登录
                    String code = ((SendAuth.Resp) resp).code;
//                    WxAuthLoginUtils.getToken(this,Constants.APP_ID,Constants.APP_SECRET,code);
                    WxAuthLoginUtils.getToken(WXEntryActivity.this, appId, appSecret, code);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Log.d(TAG, "11111 onResp cancel");
//                if (AppConfig.WX_TYPE_FRIEND == wxType || AppConfig.WX_TYPE_TIMELINE == wxType) {
//                    result = R.string.errcode_cancel;
//                } else if (AppConfig.WX_TYPE_AUTH == wxType) {
//                    result = R.string.auth_cancel;
//                }
//                Toast.makeText(WXEntryActivity.this, result, Toast.LENGTH_LONG).show();
                this.finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Log.d(TAG, "11111 onResp auth denied");
                result = R.string.errcode_deny;
                Toast.makeText(WXEntryActivity.this, result, Toast.LENGTH_LONG).show();
                this.finish();
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                Log.d(TAG, "11111 onResp err_unsupport");
                result = R.string.errcode_unsupported;
                Toast.makeText(WXEntryActivity.this, result, Toast.LENGTH_LONG).show();
                this.finish();
                break;
            default:
                Log.d(TAG, "11111 onResp unknown");
//                result = R.string.errcode_unknown;
//                Toast.makeText(WXEntryActivity.this, result, Toast.LENGTH_LONG).show();
                this.finish();
                break;
        }
    }

    //授权请求
    private void auth() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wx_login_ddq";
        MyApplication.WXapi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private void shareWX(int mTargetScene, String data) {
        if (TextUtils.isEmpty(data)) {
            Log.d(TAG, "1111111 onResp isEmpty data=" + data);
//            Toast.makeText(this, R.string.str_share_data_exception, Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }
        Log.d(TAG, "1111111 data=" + data);
        ShareData bean = new Gson().fromJson(data, ShareData.class);
        String title, desc;
        if (TextUtils.isEmpty(bean.getTitle())) {
            title = getString(R.string.app_name);
            desc = getString(R.string.str_trade_model);
        } else {
            title = bean.getTitle();
            desc = bean.getDesc();
        }
        int THUMB_SIZE = 150;
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = bean.getLink();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = desc;
        //0 分享好友  1 分享朋友圈 webpage
        Glide.with(this).asBitmap().load(bean.getImgUrl()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Log.d(TAG, "1111111 11 onResourceReady");
                Bitmap thumbBmp = Bitmap.createScaledBitmap(resource, THUMB_SIZE, THUMB_SIZE, true);
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = mTargetScene == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
                MyApplication.WXapi.sendReq(req);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                Bitmap thumbBmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_logo);
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = mTargetScene == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
                MyApplication.WXapi.sendReq(req);
            }
        });
    }
}
