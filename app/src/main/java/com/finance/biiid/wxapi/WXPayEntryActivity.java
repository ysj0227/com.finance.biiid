package com.finance.biiid.wxapi;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.finance.biiid.MyApplication;
import com.finance.biiid.R;
import com.finance.biiid.config.Constants;
import com.finance.biiid.model.PayData;
import com.finance.biiid.model.WxTokenData;
import com.finance.biiid.utils.PayDialog;
import com.finance.biiid.utils.WXPayConstants;
import com.finance.biiid.utils.WXPayUtil;
import com.finance.commonlib.http.BaseHttpApi;
import com.finance.commonlib.utils.HttpsUtils;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";
    private PayData bean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pay_result);

        Intent intent = getIntent();
        String mData = intent.getStringExtra("data");
        if (TextUtils.isEmpty(mData)) {
            Toast.makeText(this, R.string.pay_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        MyApplication.WXapi.handleIntent(getIntent(), this);
        bean = new Gson().fromJson(mData, PayData.class);
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
        Log.e(TAG, "onPayFinish, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            int errCode = resp.errCode;
            String tip = "";
            int backgroundResource;
            // 0 则代表支付成功
            // -1为支付失败，包括用户主动取消支付，或者系统返回的错误
            // -2为取消支付，或者系统返回的错误
            // 其他为系统返回的错误
            if (errCode == 0) {
                String transaction = resp.transaction;
                Log.e(TAG,"transaction="+transaction);
                tip = getString(R.string.pay_success);
                backgroundResource = R.mipmap.ic_pay_success;
//                queryOrder(transaction);
//                return;
            }else if (errCode == -1) {
                tip = getString(R.string.pay_fail);
                backgroundResource = R.mipmap.ic_pay_fail;
            } else if (errCode == -2) {
                tip = getString(R.string.pay_cancel);
                backgroundResource = R.mipmap.ic_pay_fail;
            } else {
                tip = getString(R.string.pay_error);
                backgroundResource = R.mipmap.ic_pay_fail;
            }
            PayDialog.result(this, tip, backgroundResource);
        }
    }

    /**
     * 开启支付
     */
    private void pay() {
        PayReq req = new PayReq();
        req.appId = Constants.APP_ID;
        req.partnerId = bean.getPartnerid();
        req.prepayId = bean.getPrepayid();
        req.nonceStr = bean.getNoncestr();
        req.timeStamp = bean.getTimestamp() + "";
        req.packageValue = bean.getPackageX();
        req.sign = bean.getSign();
        req.extData = "app data"; // optional
        MyApplication.WXapi.sendReq(req);
    }


    public Map<String, String> getPreParams(String transaction_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("appid", bean.getAppid());//App ID
        params.put("mch_id", bean.getPartnerid());//商户号
        params.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        params.put("transaction_id", transaction_id);//订单号
        try {
            params.put("sign", WXPayUtil.generateSignature(params, Constants.API_KEY));//签名
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    /**
     * 查询订单
     * @param transaction_id
     */
    private void queryOrder(String transaction_id) {
        Map<String, String> params = getPreParams(transaction_id);
        String xml = null;
        try {
            xml = WXPayUtil.mapToXml(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(xml)) {
            Log.e("TAG", "getOrder: 组装参数出错");
            return;
        }
        //requestBody
        MediaType XML = MediaType.parse("application/xml; charset=utf-8");
        RequestBody requestBody = RequestBody.create(XML, xml);
        //requestBody
        Request.Builder request = new Request.Builder()
                .url(WXPayConstants.PAY_QUERY_ORDER).post(requestBody);
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
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", "onFailure");
                PayDialog.result(WXPayEntryActivity.this, getString(R.string.pay_error), R.mipmap.ic_pay_fail);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.e("TAG", "onResponse=" + res);
//                try {
//                    Map<String, String> map = WXPayUtil.xmlToMap(res);
//                    String trade_type = map.get("trade_type");
//                    Log.i("TAG", "onResponse=" + trade_type);
//                    PayDialog.result(WXPayEntryActivity.this, getString(R.string.pay_success), R.mipmap.ic_pay_success);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });
    }
}