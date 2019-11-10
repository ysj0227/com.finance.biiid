package com.finance.biiid.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.finance.biiid.MyApplication;
import com.finance.biiid.R;
import com.finance.biiid.config.AppConfig;
import com.finance.biiid.config.Constants;
import com.finance.biiid.utils.Util;
import com.finance.biiid.utils.WXPayUtil;
import com.finance.commonlib.http.BaseHttpApi;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    /**
     * 关于微信分享成功的回调：在这里着重说明必须使用微信提供的 WXEntryActivity类
     * 否则分享回调不成功
     */
    private int wxType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_transparent);//分享设置透明布局
        try {
            MyApplication.WXapi.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        wxType = intent.getIntExtra(AppConfig.WX_TYPE, 0);
        if (AppConfig.WX_TYPE_FRIEND == wxType || AppConfig.WX_TYPE_TIMELINE == wxType) {
            shareWX(wxType, null);
        } else if (AppConfig.WX_TYPE_AUTH == wxType) {
            auth();
        }

//		  通过WXAPIFactory工厂，获取IWXAPI的实例(已在Application 实例化)
//        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
//        api.registerApp(Constants.APP_ID);

        //获取分享的传值  //targetScene 0 分享好友  1 分享朋友圈
//        Bundle bundle = getIntent().getExtras();
//        Bitmap bitmap = bundle.getParcelable("bitmap");
//        int targetScene = bundle.getInt("targetScene");

        /**
         * 分享到好友，朋友圈
         */
//        shareWX(0, null);
//        shareText(0, "的点点滴滴");
        /**
         * 分享小程序(小程序只能分享到好友，无法分享到朋友圈)
         */
//        shareProgram(bitmap);

//        auth();
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
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (AppConfig.WX_TYPE_FRIEND == wxType || AppConfig.WX_TYPE_TIMELINE == wxType) {
                    result = R.string.errcode_success;
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                    this.finish();
                } else if (AppConfig.WX_TYPE_AUTH == wxType) {
                    String code = ((SendAuth.Resp) resp).code;
                    token(code);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (AppConfig.WX_TYPE_FRIEND == wxType || AppConfig.WX_TYPE_TIMELINE == wxType) {
                    result = R.string.errcode_cancel;
                } else if (AppConfig.WX_TYPE_AUTH == wxType) {
                    result = R.string.auth_cancel;
                }
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                this.finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                this.finish();
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = R.string.errcode_unsupported;
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                this.finish();
                break;
            default:
                result = R.string.errcode_unknown;
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                this.finish();
                break;
        }


    }

    //#########################################################################################
    private void auth() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wx_login_ddq";
        MyApplication.WXapi.sendReq(req);
    }

    private void token(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("appid", Constants.APP_ID);
        params.put("secret", Constants.APP_SECRET);
        params.put("code", code);
        params.put("grant_type", "authorization_code");

        BaseHttpApi.post("https://api.weixin.qq.com/sns/oauth2/access_token?", params, new StringCallback() {
            @Override
            public void onError(Call call, Response response, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("tag", response);
//                {"access_token":"27_YDUAfAWnfJGL4nirwyCoSah2PY8TIxs0em46AFbNrs2vmD35FkP2fCQGAGxm2oVue2IzNqoUA10ElpMZG4GwYwWHFqIBDrrfj-hPajmQBYU",
//                        "expires_in":7200,"" +
//                        "refresh_token":"27_cS7j_sax_7iIg4zbm3Ets48s5rR5QukKI-Z87udqfLBJTdTW1PWLYlq-0yRiIBgaiU2J31hNbJ8ccr3RjSJ45ceKVrBBVBqMMqtYQxSjYR4",
//                        "openid":"o6Frrw6RqIfYS0OTCXtexsqGM5R0",
//                        "scope":"snsapi_userinfo",
//                        "unionid":"o8W2Vs-FCsc3slZS7KnlqsUSENlk"}
                try {
                    JSONObject object = new JSONObject(response);
                    String access_token = object.getString("access_token");
                    String openid = object.getString("openid");
                    getUserInfo(access_token, openid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getUserInfo(String access_token, String openid) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", access_token);
        params.put("openid", openid);

        BaseHttpApi.post("https://api.weixin.qq.com/sns/userinfo?", params, new StringCallback() {
            @Override
            public void onError(Call call, Response response, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("tag", "******  = " + response);
                Toast.makeText(WXEntryActivity.this, response, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }


    //#########################################################################################


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private void shareText(int mTargetScene, String strText) {
        //初始化一个 WXTextObject 对象，填写分享的文本内容
        WXTextObject textObj = new WXTextObject();
        textObj.text = strText;
        //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        // 发送文本类型的消息时，title字段不起作用
        msg.title = "share";
        msg.description = strText;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        if (mTargetScene == 0) {
            req.scene = SendMessageToWX.Req.WXSceneSession;//好友
        } else if (mTargetScene == 1) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;//朋友圈
        }
//        req.openId= Constants.APP_ID;
        MyApplication.WXapi.sendReq(req);
        finish();
    }

    private void shareWX(int mTargetScene, Bitmap loadUrlBitmap) {
        //0 分享好友  1 分享朋友圈
        int THUMB_SIZE = 150;
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = AppConfig.URL_RELEASE;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "标题";
        msg.description = "东东抢文本信息";
        Bitmap bmp;
        if (loadUrlBitmap == null) {//如果网络加载失败直接选取本地logo
            bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_logo);
        } else {
            bmp = loadUrlBitmap; //网络图片
        }
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        if (mTargetScene == 0) {
            req.scene = SendMessageToWX.Req.WXSceneSession;//好友
        } else if (mTargetScene == 1) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;//朋友圈
        }
        MyApplication.WXapi.sendReq(req);
    }

    //分享小程序
    private void shareProgram(Bitmap loadUrlBitmap) {
        WXMiniProgramObject miniProgram = new WXMiniProgramObject();
        miniProgram.webpageUrl = "https://www.35wenku.com/track?uuid=abcdefgh";//自定义
        miniProgram.userName = "gh_9e74f9420031";//小程序端提供参数
        miniProgram.path = "pages/home/index?uuid=abcdefgh";//小程序端提供参数
        WXMediaMessage mediaMessage = new WXMediaMessage(miniProgram);
        mediaMessage.title = "小程序";//自定义
        mediaMessage.description = "这是一个小程序的分享";//自定义
        Bitmap bmp;
        if (loadUrlBitmap == null) {//如果网络加载失败直接选取本地logo
            bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_logo);
        } else {
            bmp = loadUrlBitmap; //网络图片
        }
        Bitmap sendBitmap = Bitmap.createScaledBitmap(bmp, 200, 200, true);
        bmp.recycle();
        mediaMessage.thumbData = Util.bmpToByteArray(sendBitmap, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("miniProgram");
        req.scene = SendMessageToWX.Req.WXSceneSession;
        req.message = mediaMessage;
        MyApplication.WXapi.sendReq(req);
    }


}