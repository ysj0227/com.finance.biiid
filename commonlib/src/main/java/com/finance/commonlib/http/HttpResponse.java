package com.finance.commonlib.http;

import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Description:
 * Created by bruce on 2019/1/28.
 */
public class HttpResponse implements Serializable {
    /**
     * code : 1
     * data : [{"token":"eyJhbGc...","uid":245,"username":"","email":"","phone":"15021550047"}]
     * msg :
     */

    private int code;
    private String msg;
    private String data;

    public HttpResponse(String res) {
        deserializer(res);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public <T> T getDataStr(Type classOfT) {
        return new GsonBuilder().create().fromJson(data, classOfT);
    }

    public void setData(String data) {
        this.data = data;
    }

    private void deserializer(String jsonString) {
        try {
            JSONObject object = new JSONObject(jsonString);
            if (object.has("code"))
                code = object.getInt("code");
            if (object.has("msg"))
                msg = object.getString("msg");
            if (object.has("data")) {
                Object dataObject = new JSONTokener(object.getString("data")).nextValue();
                if (dataObject instanceof JSONObject) {
                    data = dataObject.toString();
                } else if (dataObject instanceof JSONArray) {
                    JSONArray jsonArray = object.getJSONArray("data");
                    if (jsonArray.length() > 0)
                        data = jsonArray.opt(0).toString();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
