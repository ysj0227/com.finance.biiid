package com.finance.commonlib.utils;

import android.content.Context;

import org.litepal.LitePal;
import org.litepal.LitePalDB;

/**
 * Description:
 * Created by bruce on 2019/9/2.
 */
public class DBUtils {
    public static void initDb(Context context) {
        LitePal.initialize(context);
//        if (!TextUtils.isEmpty(SpUtils.getUID())
//                && SpUtils.isLoginSuccess()) {
//            LitePalDB litePalDB = LitePalDB.fromDefault(SpUtils.getUID());
//            LitePal.use(litePalDB);
//        }
    }

    public static void switchDb(String name) {
        LitePalDB litePalDB = LitePalDB.fromDefault(name);
        LitePal.use(litePalDB);
    }


}
