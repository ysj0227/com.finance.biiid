package com.finance.commonlib.http;

import com.finance.commonlib.utils.log.LogCat;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;


/**
 * Description:
 * Created by bruce on 2019/1/24.
 */
public class BaseHttpApi {
    public static final String TAG = "HttpCall";
    //net connect time
    public static final int CONN_TIMEOUT = 1000 * 10;

    /**
     * okHttp post
     *
     * @param url      地址
     * @param map      传递参数
     * @param callback 回调处理
     */
    public static void post(String url, Map<String, String> map, StringCallback callback) {
        LogCat.e(TAG, "post: url = " + url + ", map = " + map);
        OkHttpUtils.post()
                .url(url).params(map).build()//request Call
                .execute(callback);
    }

    public static void get(String url, Map<String, String> map, StringCallback callback) {
        LogCat.e(TAG, "post: url = " + url + ", map = " + map);
        OkHttpUtils.get()
                .url(url).params(map).build()//request Call
                .execute(callback);
    }

    /**
     * okHttp post
     *
     * @param url      地址
     * @param map      传递参数
     * @param timeout  超时时间 s
     * @param callback 回调处理
     */
    public static void post(String url, Map<String, String> map, int timeout, StringCallback callback) {
        LogCat.e(TAG, "post: url = " + url + ", map = " + map);
        OkHttpUtils.post()
                .url(url)
                .params(map).build()//request Call
                .connTimeOut(timeout * 1000)
                .readTimeOut(timeout * 1000)
                .execute(callback);
    }
}
