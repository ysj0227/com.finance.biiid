package com.finance.biiid.test;

import android.text.TextUtils;
import android.util.Log;

import com.finance.biiid.MyApplication;
import com.finance.biiid.config.Constants;
import com.finance.biiid.utils.WXPayUtil;
import com.finance.commonlib.utils.HttpsUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;

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

public class TestPay {

    public static void pay(String prepayid) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String noncestr = WXPayUtil.generateNonceStr();
        String partnerId = Constants.MCH_ID;
        String packageValue = "Sign=WXPay";
        HashMap<String, String> params = new HashMap<>();
        params.put("appid", Constants.APP_ID);//App ID
        params.put("partnerid", partnerId);//商户号
        params.put("prepayid", prepayid); //微信返回的支付交易会话ID
        params.put("noncestr", noncestr);//随机字符串
        params.put("package", packageValue);//固定Sign=WXPay
        params.put("timestamp", timestamp);//10位
        String sign = null;
        try {
            sign = WXPayUtil.generateSignature(params, Constants.API_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PayReq req = new PayReq();
        req.appId = Constants.APP_ID;
        req.partnerId = partnerId;
        req.prepayId = prepayid;
        req.nonceStr = noncestr;
        req.timeStamp = timestamp;
        req.packageValue = packageValue;
        req.sign = sign;
        req.extData = "app data"; // optional
        MyApplication.WXapi.sendReq(req);
    }

    public static Map<String, String> getPreParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("appid", Constants.APP_ID);//App ID
        params.put("body", "东东抢艺术品");//商品名称
        params.put("mch_id", Constants.MCH_ID);//商户号
        params.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        params.put("notify_url", "http://www.weixin.qq.com/wxpay/pay.php");//支付结果回调地址
        params.put("out_trade_no", WXPayUtil.generateUUID());//订单号
        params.put("spbill_create_ip", "127.0.0.1");//  用户端实际ip
        params.put("total_fee", "1");//金额
        params.put("trade_type", "APP");//支付类型
        try {
            params.put("sign", WXPayUtil.generateSignature(params, Constants.API_KEY));//签名
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    /**
     * 下单
     */
    public static void getOrder() {
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        Map<String, String> params = getPreParams();
        String xml = null;
        try {
            xml = WXPayUtil.mapToXml(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(xml)) {
            Log.i("TAG", "getOrder: 组装参数出错");
            return;
        }
        Log.i("统一下单参数：", xml);
        //requestBody
        MediaType XML = MediaType.parse("application/xml; charset=utf-8");
        RequestBody requestBody = RequestBody.create(XML, xml);
        //requestBody
        Request.Builder request = new Request.Builder()
                .url(url).post(requestBody);
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
                Log.i("TAG", "onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.i("TAG", "onResponse=" + res);
                try {
                    Map<String, String> map = WXPayUtil.xmlToMap(res);
                    String prepay_id = map.get("prepay_id");
                    Log.i("TAG", "onResponse=" + prepay_id);
                    pay(prepay_id);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
