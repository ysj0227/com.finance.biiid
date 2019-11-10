package com.finance.commonlib.http;

import android.content.Context;

import com.finance.commonlib.base.BaseActivity;
import com.finance.commonlib.utils.log.LogCat;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

public abstract class RpcCallback1 extends StringCallback {
    private Context context;

    public RpcCallback1(Context context) {
        this.context = context;
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).showLoadingDialog();
        }
    }

    public RpcCallback1(Context context, boolean isShowLoading) {
        this.context = context;
        if (isShowLoading && context instanceof BaseActivity) {
            ((BaseActivity) context).showLoadingDialog();
        }
    }
//
//    @Override
//    public void onError(Call call, Response response, Exception e, int id) {
//        LogCat.e("RpcCallback", "onError, response = " + response);
//        networkErrorHandler(response);
////            if (response != null)
////                onError(response.code(), response.message(), response.body().string());
//    }
//
//    private void networkErrorHandler(Response response) {
//        if (context instanceof BaseActivity) {
//            ((BaseActivity) context).hideLoadingDialog();
//            ((BaseActivity) context).shortTip(NetworkUtils.isNetworkAvailable(context)
//                    ? R.string.toast_network_Exception : R.string.network_wifi_low);
//        }
//    }

    @Override
    public void onResponse(String response, int id) {
        LogCat.e("RpcCallback", "onResponse, response =:" + response);
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).hideLoadingDialog();
        }
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt("code");
            onSuccess(code, jsonObject.getString("msg"),
                    jsonObject.getString("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Call call, Response response, Exception e, int id) {
        onError(response, e);
    }

    public abstract void onSuccess(int code, String msg, String data);

    public abstract void onError(Response response, Exception e);

}
