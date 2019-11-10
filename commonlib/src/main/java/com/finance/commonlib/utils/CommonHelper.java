package com.finance.commonlib.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.LocaleList;
import android.text.Selection;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.finance.commonlib.utils.log.LogCat;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Objects;


public class CommonHelper {

    public static String getLanguage() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale.getLanguage().toLowerCase() + "_" + locale.getCountry().toLowerCase();
    }

    /**
     * 获取类名称
     *
     * @param e 异常类对象(eg:new Exception())
     * @return 返回类名称
     */
    public static String getCName(Exception e) {
        return e.getStackTrace()[0].getClassName();
    }

    /**
     * 获取手机屏幕的宽高
     * activity 上下文
     *
     * @return 返回屏幕的宽高点（即右下角的点）
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static Point getScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point;
    }

    /**
     * 获取手机屏幕的宽度（像素）
     *
     * @param activity 上下文
     * @return 返回手机屏幕的宽度
     */
    public static int getScreenWidth(Context activity) {
        return getScreenSize(activity).x;
    }

    /**
     * 获取手机屏幕的高度（像素）
     *
     * @param activity 上下文
     * @return 返回手机屏幕高度
     */
    public static int getScreenHeight(Context activity) {
        return getScreenSize(activity).y;
    }

    /**
     * 根据手机的分辨率从单位dp转成为单位 px(像素)
     *
     * @param context 上下文
     * @param dpValue 设置的dp值
     * @return 返回转换后的px值
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从单位px(像素)转成单位dp
     *
     * @param context 上下文
     * @param pxValue 设置的px值
     * @return 返回转换之后的dp值
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 判断当前手机是否有Root权限
     *
     * @return true-有;false-无
     */
    public static boolean isRoot() {
        boolean result = false;

        try {
            result = (new File("/system/bin/su").exists()) || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage());
        }

        return result;
    }

    /**
     * 获取应用程序包名称
     *
     * @param context 上下文
     * @return 返回应用程序包名称
     */
    public static String getAppPackageName(Context context) {
        String appInfo = "";

        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage());
        }

        if (mPackageInfo != null) {
            appInfo = mPackageInfo.packageName;
        }
        return appInfo;
    }

    /**
     * 获取应用程序版本名称
     *
     * @param context 上下文
     * @return 返回应用程序版本名称
     */
    public static String getAppVersionName(Context context) {
        String appInfo = "";

        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage());
        }

        if (mPackageInfo != null) {
            appInfo = mPackageInfo.versionName;
        }
        return appInfo;
    }

    /**
     * 获取应用程序信息
     * context 上下文
     *
     * @return 返回应用程序版本代码
     */
    public static int getAppVersionCode(Context pContext) {
        int appInfo = -1;

        PackageManager mPackageManager = pContext.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(pContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
        }

        if (mPackageInfo != null) {
            appInfo = mPackageInfo.versionCode;
        }
        return appInfo;
    }

    /**
     * 拨打电话
     *
     * @param activity 当前Activity
     * @param phoneNO  电话号码
     */
    public static void tel(Activity activity, String phoneNO) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNO));
        activity.startActivity(intent);
    }

    /**
     * 发送短信
     *
     * @param activity 当前Activity
     * @param to
     * @param content
     */

    public static void sendSMS(Activity activity, String to, String content) {
        Uri smsToUri = Uri.parse("smsto:" + to);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", content);
        activity.startActivity(intent);
    }

    public static void installShortcut(Context context, String appName, Class<?> mainClass, Bitmap icon) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setClass(context, mainClass);
        installShortcut(context, appName, mainIntent, icon);
    }

    public static void installShortcut(Context context, String appName, Intent mainIntent, Bitmap icon) {
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
        // 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加  
        shortcutIntent.putExtra("duplicate", false);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, mainIntent);
//        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.icon));  
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
        context.sendBroadcast(shortcutIntent);
    }

    public static void openFile(Activity activity, File f) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        String type = getMIMEType(f);
        intent.setDataAndType(Uri.fromFile(f), type);
        activity.startActivity(intent);
    }

    public static String getMIMEType(File f) {
        String end = f.getName().substring(f.getName().lastIndexOf(".") + 1).toLowerCase();
        String type = "";
        if (end.equals("mp3") || end.equals("aac") || end.equals("aac")
                || end.equals("amr") || end.equals("mpeg") || end.equals("mp4")
                || end.equals("m4a")) {
            type = "audio";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg")) {
            type = "image";
        } else {
            type = "*";
        }
        type += "/*";
        return type;
    }

    //根据字符串型的资源名获取对应资源id
    public static int getResource(Context ctx, String imageName, int defResId) {
        int resId = ctx.getResources().getIdentifier(imageName, "mipmap", ctx.getPackageName());
        if (resId == 0) {  //如果没有在"mipmap"下找到imageName,将会返回0
            resId = defResId;//设置默认
        }
        return resId;
    }

    /**
     * @param button  button
     * @param isClick 按钮是否可点击
     */
    public static void isCanClick(Button button, boolean isClick) {
        if (isClick) {
            button.setAlpha(1f);
            button.setEnabled(true);
        } else {
            button.setAlpha(0.5f);
            button.setEnabled(false);
        }
    }

    /**
     * 如果小数点后为零显示整数否则保留
     *
     * @param num
     * @return
     */
    public static String floatTrans(float num) {
        if (Math.round(num) - num == 0) {
            return String.valueOf((int) num);
        }
        return String.valueOf(num);
    }


    /**
     * 光标后置
     *
     * @param et 输入框
     */
    public static void setSelectionEnd(EditText et) {
        String str = et.getText().toString();
        if (TextUtils.isEmpty(str)) {
            return;
        }
        Selection.setSelection(et.getText(), str.length());
    }

    public static int statusHeight(Context context) {
        int statusBarHeight = -1;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) Objects.requireNonNull(context
                .getSystemService(Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {    // 当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {    // 当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());    // 得到IPV4地址
                return ipAddress;
            }
        } else {
            // 当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

}
