package com.finance.biiid.wxapi;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.finance.biiid.MyApplication;
import com.finance.biiid.R;
import com.finance.biiid.test.TestPay;
import com.finance.biiid.utils.PayDialog;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pay_result);

        MyApplication.WXapi.handleIntent(getIntent(), this);

        TestPay.getOrder();
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
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            int errCode = resp.errCode;
            String tip = "";
            int backgroundResource;
            // 0 则代表支付成功
            // -1为支付失败，包括用户主动取消支付，或者系统返回的错误
            // -2为取消支付，或者系统返回的错误
            // 其他为系统返回的错误
            if (errCode == 0) {
                //todo 查询服务器判断是否真正支付成功
                tip = "支付成功";
                backgroundResource = R.mipmap.ic_pay_success;
            } else if (errCode == -1) {
                tip = "支付失败";
                backgroundResource = R.mipmap.ic_pay_fail;
            } else if (errCode == -2) {
                tip = "取消支付";
                backgroundResource = R.mipmap.ic_pay_fail;
            } else {
                tip = "支付错误";
                backgroundResource = R.mipmap.ic_pay_fail;
            }
            PayDialog.result(this, tip, backgroundResource);
        }
    }

    private void pay() {
//        BaseHttpApi.post("", null, new StringCallback() {
//            @Override
//            public void onError(Call call, Response response, Exception e, int id) {
//
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//
//            }
//        });

//        PayReq req = new PayReq();
//        req.appId = Constants.APP_ID;
//        req.partnerId = partnerId;
//        req.prepayId = prepayid;
//        req.nonceStr = noncestr;
//        req.timeStamp = timestamp;
//        req.packageValue = packageValue;
//        req.sign = sign;
//        req.extData = "app data"; // optional
//        MyApplication.WXapi.sendReq(req);
    }
}