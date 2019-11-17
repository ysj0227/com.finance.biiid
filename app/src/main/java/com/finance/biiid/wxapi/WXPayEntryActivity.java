package com.finance.biiid.wxapi;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.finance.biiid.MyApplication;
import com.finance.biiid.R;
import com.finance.biiid.config.AppConfig;
import com.finance.biiid.config.Constants;
import com.finance.biiid.model.PayData;
import com.finance.biiid.notifications.CommonNotifications;
import com.finance.commonlib.notification.BaseNotification;
import com.finance.commonlib.utils.HttpsUtils;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";
    private String outTradeNo, returnUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pay_result);
        MyApplication.WXapi.handleIntent(getIntent(), this);
        Intent intent = getIntent();
        String mData = intent.getStringExtra(AppConfig.WX_DATA);
        if (TextUtils.isEmpty(mData)) {
            Toast.makeText(this, R.string.pay_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        pay();
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

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode + " , transaction=" + resp.transaction);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            int errCode = resp.errCode;
            String tip;
            // 0 则代表支付成功
            // -1为支付失败，包括用户主动取消支付，或者系统返回的错误
            // -2为取消支付，或者系统返回的错误
            // 其他为系统返回的错误
            if (errCode == 0) {
                queryTradeStatus();
                return;
            }
            if (errCode == -1) {
                tip = getString(R.string.pay_fail);
            } else if (errCode == -2) {
                tip = getString(R.string.pay_cancel);
            } else {
                tip = getString(R.string.pay_error);
            }
            result(this, tip, R.mipmap.ic_pay_fail);
        }
    }

    /**
     * 开启支付
     */
    private void pay() {
        Intent intent = getIntent();
        String mData = intent.getStringExtra(AppConfig.WX_DATA);
        PayData bean = new Gson().fromJson(mData, PayData.class);
        outTradeNo = bean.getOuttradeno();
        returnUrl = bean.getUrl();
        PayReq req = new PayReq();
        req.appId = Constants.APP_ID;
        req.partnerId = bean.getPartnerid();
        req.prepayId = bean.getPrepayid();
        req.nonceStr = bean.getNoncestr();
        req.timeStamp = bean.getTimestamp() + "";
        req.packageValue = bean.getPackageX();
        req.sign = bean.getSign();
        req.extData = "app_ddq_pay"; // optional
        MyApplication.WXapi.sendReq(req);
    }

    private void queryTradeStatus() {
        JSONObject object = new JSONObject();
        try {
            object.put("out_trade_no", outTradeNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //requestBody
        MediaType json = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, object.toString());
        //requestBody
        Request.Builder request = new Request.Builder()
                .url(AppConfig.QUERY_TRADE_STATUS).post(requestBody);
        //OkHttpClient
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        mBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        mBuilder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
        OkHttpClient okHttpClient = mBuilder.build();
        //call
        Call call = okHttpClient.newCall(request.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("TAG", "pay trade onFailure");
                postJsPaySuccess(-1);
                result(WXPayEntryActivity.this, getString(R.string.pay_exception), R.mipmap.ic_pay_fail);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                assert response.body() != null;
                String res = response.body().string();
                Log.d("TAG", "pay trade onResponse=" + res);
                queryResult(res);
            }
        });
    }

    private void queryResult(String response) {
        try {
            JSONObject object = new JSONObject(response);
            if (!object.has("code")) {
                postJsPaySuccess(-1);
                result(WXPayEntryActivity.this, getString(R.string.pay_error), R.mipmap.ic_pay_fail);
                return;
            }
            int code = object.getInt("code");
            String msg = object.getString("msg");
            postJsPaySuccess(code);
            if (code == 0) {
                result(WXPayEntryActivity.this, getString(R.string.pay_success), R.mipmap.ic_pay_success);
            } else {
                result(WXPayEntryActivity.this, TextUtils.isEmpty(msg) ? getString(R.string.pay_exception) : msg, R.mipmap.ic_pay_fail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void postJsPaySuccess(int code) {
        BaseNotification.newInstance().postNotificationName(CommonNotifications.weChatPayStatus, code, returnUrl);
    }

    /**
     * 支付结果dialog
     * @param getActivity getActivity
     * @param tip tip
     * @param backgroundResource backgroundResource
     */
    private void result(Activity getActivity, String tip, int backgroundResource) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            Dialog dialog = new Dialog(getActivity, R.style.BottomDialog);
            View inflate = LayoutInflater.from(getActivity).inflate(R.layout.dialog_pay, null);
            ImageView ivPayIcon = inflate.findViewById(R.id.iv_pay_icon);
            TextView tvResult = inflate.findViewById(R.id.tv_result);
            Button btnSure = inflate.findViewById(R.id.btn_sure);
            ivPayIcon.setBackgroundResource(backgroundResource);
            tvResult.setText(tip);
            btnSure.setOnClickListener(v -> getActivity.finish());
            dialog.setContentView(inflate);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        });
    }
}