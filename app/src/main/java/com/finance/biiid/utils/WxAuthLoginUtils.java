package com.finance.biiid.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.finance.biiid.R;
import com.finance.biiid.model.WxTokenData;
import com.finance.biiid.notifications.CommonNotifications;
import com.finance.commonlib.http.BaseHttpApi;
import com.finance.commonlib.notification.BaseNotification;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class WxAuthLoginUtils {

    /**
     * 获取微信token
     *
     * @param appid
     * @param secret
     * @param code
     */
    public static void getToken(Activity context, String appid, String secret, String code) {
        Map<String, String> params = new HashMap<>();
        params.put("appid", appid);
        params.put("secret", secret);
        params.put("code", code);
        params.put("grant_type", "authorization_code");
        BaseHttpApi.post(WXPayConstants.AUTH_ACCESS_TOKEN, params, new StringCallback() {
            @Override
            public void onError(Call call, Response response, Exception e, int id) {
                toastAuthFail(context);
                context.finish();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.d("tag", response);
                getUserInfo(context, response);
            }
        });
    }

    /**
     * 获取用户信息
     *
     * @param data
     */
    public static void getUserInfo(Activity context, String data) {
        WxTokenData bean = new Gson().fromJson(data, WxTokenData.class);
        SpUtils.saveWXRefreshToken(bean.getRefresh_token());
        SpUtils.saveWXUnionid(bean.getUnionid());
        Map<String, String> params = new HashMap<>();
        params.put("access_token", bean.getAccess_token());
        params.put("openid", bean.getOpenid());

        BaseHttpApi.post(WXPayConstants.USER_INFO, params, new StringCallback() {
            @Override
            public void onError(Call call, Response response, Exception e, int id) {
                toastAuthFail(context);
                context.finish();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.d("tag", "******  = " + response);
                BaseNotification.newInstance().postNotificationName(CommonNotifications.weChatData, data, response);
                context.finish();
            }
        });
    }

    private static void toastAuthFail(Context context) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> Toast.makeText(context, context.getString(R.string.auth_fail), Toast.LENGTH_SHORT).show());
    }

}
