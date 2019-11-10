package com.finance.commonlib.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.util.List;

public class NetworkUtils {

    /**
     * 检测网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        boolean isNetworkAvailable = false;
        if (context == null) return false;
        ConnectivityManager connectivity =
                (ConnectivityManager) (context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo[] networkInfos = connectivity.getAllNetworkInfo();
        if (networkInfos == null)
            return false;

        for (NetworkInfo itemInfo : networkInfos) {
            if (itemInfo != null) {
                if (itemInfo.getState() == NetworkInfo.State.CONNECTED
                        || itemInfo.getState() == NetworkInfo.State.CONNECTING) {
                    isNetworkAvailable = true;
                    break;
                }
            }
        }
        return isNetworkAvailable;
    }

    /**
     * 判断GPS是否可用
     *
     * @param context 上下文
     * @return 可用：true;不可用：false
     */
    public static boolean isGPSAvailable(Context context) {
        boolean result = false;

        LocationManager mLocationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        List<String> mProviders = mLocationManager.getProviders(true);
        if (mProviders != null && mProviders.size() > 0) {
            result = true;
        }

        return result;
    }

    /**
     * 判断当前网络是否可用(通用方法)
     * 耗时12秒
     *
     * @return
     */
    public static boolean isNetPingUsable() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("ping -c 3 www.baidu.com");
            int ret = process.waitFor();
            if (ret == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断WIFI是否打开
     *
     * @param context
     * @return
     */
    public static boolean isWifiOpen(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 判断是否是(2,3,4)G网络
     *
     * @param context
     * @return
     */
    public static boolean isMobile(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * 判断是wifi还是(2,3,4)G网络
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 获取信号ssid
     *
     * @param context
     * @return
     */
    public static String getSSID(Context context, String model) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        String ssid = mWifiInfo.getSSID();
        String name;
        if (ssid.contains("\"")) {
            name = ssid.replace("\"", "");//去除引号
        } else if (ssid.contains("unknown")) {
            name = model;
        } else {
            name = ssid;
        }
        return name;
    }

}
