package com.finance.commonlib.http;

import android.content.Context;

import com.finance.commonlib.R;
import com.finance.commonlib.base.BaseActivity;
import com.finance.commonlib.utils.NetworkUtils;
import com.finance.commonlib.utils.log.LogCat;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

public abstract class RpcCallback extends StringCallback {
    private Context context;

    public RpcCallback(Context context) {
        this.context = context;
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).showLoadingDialog();
        }
    }

    public RpcCallback(Context context, boolean isShowLoading) {
        this.context = context;
        if (isShowLoading && context instanceof BaseActivity) {
            ((BaseActivity) context).showLoadingDialog();
        }
    }

    @Override
    public void onError(Call call, Response response, Exception e, int id) {
        LogCat.e("RpcCallback", "onError, response = " + response);
        networkErrorHandler(response);
//            if (response != null)
//                onError(response.code(), response.message(), response.body().string());
    }

    private void networkErrorHandler(Response response) {
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).hideLoadingDialog();
            if (!NetworkUtils.isNetworkAvailable(context)) {
                ((BaseActivity) context).shortTip(R.string.network_wifi_low);
            }
        }
    }

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

    public void onError(int code, String msg, String data) {

    }

    public abstract void onSuccess(int code, String msg, String data);

}
