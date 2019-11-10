package com.finance.commonlib.http;

import com.finance.commonlib.R;
import com.finance.commonlib.base.BaseApplication;
import com.finance.commonlib.base.BaseView;
import com.finance.commonlib.utils.NetworkUtils;
import com.finance.commonlib.utils.log.LogCat;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Description:
 * Created by bruce on 2019/1/28.
 */
public abstract class HttpCallback<T> extends StringCallback {
    private BaseView view;
    Type type;

    protected HttpCallback(BaseView view) {
        this.view = view;
        if (view != null) {
            view.showLoadingDialog();
        }
    }

    @Override
    public void onError(Call call, Response response, Exception e, int id) {
        LogCat.e("HttpCallback", "onError, e = " + e);
        networkErrorHandler(id, response);
    }

    private void networkErrorHandler(int id, Response response) {
        if (view != null) {
            view.hideLoadingDialog();
            if (!NetworkUtils.isNetworkAvailable(BaseApplication.getContext())) {
                view.shortTip(R.string.network_wifi_low);
                return;
            }
        }
        onFail(id, "", "");
//        if (response != null) onFail(response.code(), response.message(), "");
    }

    @Override
    public void onResponse(String response, int id) {
        LogCat.e("HttpCallback", "onResponse, response = " + response);
        if (view != null) {
            view.hideLoadingDialog();
        }
        HttpResponse res = new HttpResponse(response);
        if (res.getCode() == 1) {
            try {
                type = getTClass();
                if (type == String.class)
                    onSuccess(res.getCode(), res.getMsg(), (T) res.getData());
                else
                    onSuccess(res.getCode(), res.getMsg(), (T) res.getDataStr(getTClass()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            onFail(res.getCode(), res.getMsg(), res.getData());
        }
    }

    private Class<T> getTClass() {
        return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void onFail(int code, String msg, String data) {

    }

    public abstract void onSuccess(int code, String msg, T data);

}
