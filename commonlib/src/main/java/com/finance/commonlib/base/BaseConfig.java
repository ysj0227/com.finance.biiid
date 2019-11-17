package com.finance.commonlib.base;

import android.content.Context;
import android.text.TextUtils;

public abstract class BaseConfig {
    //测试
    public static final String ENV_TEST = "ENV_TEST";
    //生产
    public static final String ENV_RELEASE = "ENV_RELEASE";

    public void init(Context context, String env) {
      if (TextUtils.equals(ENV_TEST, env)) {
            initTest(context, env);
        } else if (TextUtils.equals(ENV_RELEASE, env)) {
            initRelease(context, env);
        }
    }
    protected abstract void initTest(Context context, String env);

    protected abstract void initRelease(Context context, String env);

}
