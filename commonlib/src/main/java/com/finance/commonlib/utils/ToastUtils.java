package com.finance.commonlib.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.DrawableRes;

/**
 * Toast统一处理工具类
 */
public class ToastUtils {
    private static long lastClickTime;
    private static Toast toast;
    private static Toast mToast;

    /**
     * 长时间弹出消息提示
     */
    public static void toastForLong(Context context, int res) {
        if (context == null || res == 0) {
            return;
        }
        toastForLong(context, context.getResources().getString(res));
    }

    /**
     * 长时间弹出消息提示
     */
    public static void toastForLong(final Context context, final String msg) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (context == null || TextUtils.isEmpty(msg)) {
                    return;
                }

                if (mToast == null) {
                    mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
                } else {
                    mToast.setText(msg);
                    mToast.setDuration(Toast.LENGTH_LONG);
                }
                mToast.show();
            }
        });
    }

    /**
     * 短时间弹出消息提示
     */
    public static void toastForShort(Context context, int res) {
        if (res <= 0 || context == null) return;
        toastForShort(context, context.getResources().getString(res));
    }

    /**
     * 短时间弹出消息提示
     */
    public static void toastForShort(final Context context, final String msg) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (context == null || TextUtils.isEmpty(msg)) {
                    return;
                }

                if (mToast == null) {
                    mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                } else {
                    mToast.setText(msg);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                mToast.show();
            }
        });
    }

    /**
     * 自定义时间弹出消息提示
     */
    public static void toastForTime(Context context, int res, int duration) {
        if (res <= 0) return;
        String msg = context.getResources().getString(res);
        toastForTime(context, msg, duration);
    }

    /**
     * 自定义时间弹出消息提示
     */
    public static void toastForTime(Context context, String msg, int duration) {
        if (TextUtils.isEmpty(msg)) return;

        if (mToast == null) {
            mToast = Toast.makeText(context, msg, duration);
        } else {
            mToast.setText(msg);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    public static void toastCenter(final Context context, final String msg, final @DrawableRes int resId) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (context == null || TextUtils.isEmpty(msg)) {
                    return;
                }

                toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                if (resId != 0) {
                    LinearLayout toastView = (LinearLayout) toast.getView();
                    ImageView image = new ImageView(context);
                    image.setImageResource(resId);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    params.setMargins(50, 40, 50, 40);
                    image.setLayoutParams(params);
                    toastView.addView(image, 0);
                }
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    public static void showCenterToast(String text, Context context) {
        showCenterToast(text, context, 0);
    }

    private static void showCenterToast(String text, Context context, int length) {
        if (toast == null)
            toast = Toast.makeText(context, text, length);
        else {
            toast.setText(text);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
