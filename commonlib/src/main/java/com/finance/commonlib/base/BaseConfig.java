package com.finance.commonlib.base;

import android.content.Context;
import android.text.TextUtils;

/**
 * Description:
 * Created by bruce on 2019/2/13.
 */
public abstract class BaseConfig {
    //开发
    public static final String ENV_DEV = "ENV_DEV";
    //测试
    public static final String ENV_TEST = "ENV_TEST";
    //UAT
    public static final String ENV_UAT = "ENV_UAT";
    //生产
    public static final String ENV_RELEASE = "ENV_RELEASE";

    public void init(Context context, String env) {
        if (TextUtils.equals(ENV_DEV, env)) {
            initDev(context, env);
        } else if (TextUtils.equals(ENV_TEST, env)) {
            initTest(context, env);
        } else if (TextUtils.equals(ENV_UAT, env)) {
            initUat(context, env);
        } else if (TextUtils.equals(ENV_RELEASE, env)) {
            initRelease(context, env);
        }
    }

    protected abstract void initDev(Context context, String env);

    protected abstract void initTest(Context context, String env);

    protected abstract void initRelease(Context context, String env);

    protected abstract void initUat(Context context, String env);

}
