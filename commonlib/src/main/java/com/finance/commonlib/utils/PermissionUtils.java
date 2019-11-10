package com.finance.commonlib.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import android.widget.Toast;

import com.finance.commonlib.R;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


/**
 * Created by admin on 2018/6/7.
 * 权限申请
 */
public class PermissionUtils {
    public static final int REQ_PERMISSIONS_STORAGE = 0x101;
    public static final int REQ_PERMISSIONS_CAMERA_STORAGE = 0x102;

    //* 如果SDK版本大于等于23、或者Android系统版本是6.0以上，那么需要动态请求创建文件的权限 */
    public static void checkPermissionActivity(Activity activity) {
        // 读,写,手机,定位,相机,短信权限
        int REQUEST_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.READ_PHONE_STATE",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.RECEIVE_SMS",
                "android.permission.READ_SMS",
                "android.permission.WRITE_SETTINGS",//百度用到
                "android.permission.REQUEST_INSTALL_PACKAGES"};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission0 = ContextCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[0]);
            int permission2 = ContextCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[2]);
            int permission3 = ContextCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[3]);
            int permission4 = ContextCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[4]);
            int permission5 = ContextCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[5]);
            int permission7 = ContextCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[7]);
            int permission8 = ContextCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[8]);
            if (permission0 != PackageManager.PERMISSION_GRANTED &&
                    permission2 != PackageManager.PERMISSION_GRANTED &&
                    permission3 != PackageManager.PERMISSION_GRANTED &&
                    permission4 != PackageManager.PERMISSION_GRANTED &&
                    permission5 != PackageManager.PERMISSION_GRANTED &&
                    permission7 != PackageManager.PERMISSION_GRANTED &&
                    permission8 != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{
                        PERMISSIONS_STORAGE[0],
                        PERMISSIONS_STORAGE[1],
                        PERMISSIONS_STORAGE[2],
                        PERMISSIONS_STORAGE[3],
                        PERMISSIONS_STORAGE[4],
                        PERMISSIONS_STORAGE[5],
                        PERMISSIONS_STORAGE[6],
                        PERMISSIONS_STORAGE[7],
                        PERMISSIONS_STORAGE[8]
                }, REQUEST_STORAGE);
            }
        }
    }

    //* 如果SDK版本大于等于23、或者Android系统版本是6.0以上，那么需要动态请求创建文件的权限 */
    public static void checkPermission(FragmentActivity getActivity) {
        // 读,写,手机,定位,相机,短信权限
        int REQUEST_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                "android.permission.READ_PHONE_STATE",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.CAMERA",
                "android.permission.RECEIVE_SMS",
                "android.permission.READ_SMS",
                "android.permission.WRITE_SETTINGS"};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int call = ContextCompat.checkSelfPermission(getActivity, PERMISSIONS_STORAGE[0]);
            int read = ContextCompat.checkSelfPermission(getActivity, PERMISSIONS_STORAGE[1]);
            int write = ContextCompat.checkSelfPermission(getActivity, PERMISSIONS_STORAGE[2]);
            int coarse_location = ContextCompat.checkSelfPermission(getActivity, PERMISSIONS_STORAGE[3]);
            int fine_location = ContextCompat.checkSelfPermission(getActivity, PERMISSIONS_STORAGE[4]);
            int camera = ContextCompat.checkSelfPermission(getActivity, PERMISSIONS_STORAGE[5]);
            int SMSreceive = ContextCompat.checkSelfPermission(getActivity, PERMISSIONS_STORAGE[6]);
            int SMSread = ContextCompat.checkSelfPermission(getActivity, PERMISSIONS_STORAGE[7]);
            int setIng = ContextCompat.checkSelfPermission(getActivity, PERMISSIONS_STORAGE[8]);

            if (call == PackageManager.PERMISSION_GRANTED &&
                    read == PackageManager.PERMISSION_GRANTED &&
                    write == PackageManager.PERMISSION_GRANTED &&
                    coarse_location == PackageManager.PERMISSION_GRANTED &&
                    fine_location == PackageManager.PERMISSION_GRANTED &&
                    camera == PackageManager.PERMISSION_GRANTED &&
                    SMSreceive == PackageManager.PERMISSION_GRANTED &&
                    SMSread == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity, getActivity.getResources().getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
            }
            if (call != PackageManager.PERMISSION_GRANTED ||
                    read != PackageManager.PERMISSION_GRANTED ||
                    write != PackageManager.PERMISSION_GRANTED ||
                    coarse_location != PackageManager.PERMISSION_GRANTED ||
                    fine_location != PackageManager.PERMISSION_GRANTED ||
                    camera != PackageManager.PERMISSION_GRANTED ||
                    SMSreceive != PackageManager.PERMISSION_GRANTED ||
                    SMSread != PackageManager.PERMISSION_GRANTED ||
                    setIng != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity, new String[]{
                        PERMISSIONS_STORAGE[0],
                        PERMISSIONS_STORAGE[1],
                        PERMISSIONS_STORAGE[2],
                        PERMISSIONS_STORAGE[3],
                        PERMISSIONS_STORAGE[4],
                        PERMISSIONS_STORAGE[5],
                        PERMISSIONS_STORAGE[6],
                        PERMISSIONS_STORAGE[7],
                        PERMISSIONS_STORAGE[8]}, REQUEST_STORAGE);
            }
        }
    }

    // SD卡,相机,录音
    public static boolean checkCameraPermission(Activity activity) {
        int REQUEST_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission1 = ContextCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[0]);
            int permission2 = ContextCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[1]);
            int permission3 = ContextCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[2]);
            if (permission1 != PackageManager.PERMISSION_GRANTED ||
                    permission2 != PackageManager.PERMISSION_GRANTED ||
                    permission3 != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{
                        PERMISSIONS_STORAGE[0], PERMISSIONS_STORAGE[1],
                        PERMISSIONS_STORAGE[2]}, REQUEST_STORAGE);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    // SD卡,相机
    public static boolean checkSDCardCameraPermission(Activity activity) {
        String[] PERMISSIONS_STORAGE = {Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission1 = ContextCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[0]);
            int permission2 = ContextCompat.checkSelfPermission(activity, PERMISSIONS_STORAGE[1]);
            if (permission1 != PackageManager.PERMISSION_GRANTED ||
                    permission2 != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{
                        PERMISSIONS_STORAGE[0], PERMISSIONS_STORAGE[1]}, REQ_PERMISSIONS_CAMERA_STORAGE);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    // SD卡
    public static boolean checkStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        int permission2 = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission2 == PackageManager.PERMISSION_GRANTED) return true;
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_PERMISSIONS_CAMERA_STORAGE);
        return false;
    }

    //* 如果SDK版本大于等于23、或者Android系统版本是6.0以上，那么需要动态请求创建文件的权限 */
    public static boolean getLocationPermission(FragmentActivity getActivity) {
        // 读,写,手机,定位,相机,短信权限
        int REQUEST_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION"};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int coarse_location = ContextCompat.checkSelfPermission(getActivity, PERMISSIONS_STORAGE[0]);
            int fine_location = ContextCompat.checkSelfPermission(getActivity, PERMISSIONS_STORAGE[1]);
            if (coarse_location != PackageManager.PERMISSION_GRANTED ||
                    fine_location != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity, new String[]{
                        PERMISSIONS_STORAGE[0],
                        PERMISSIONS_STORAGE[1],}, REQUEST_STORAGE);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    //* 如果SDK版本大于等于23、或者Android系统版本是6.0以上，那么需要动态请求创建文件的权限 */
    public static boolean checkLocationPermission(Context context) {
        String[] PERMISSIONS_STORAGE = {"android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int coarse_location = ContextCompat.checkSelfPermission(context, PERMISSIONS_STORAGE[0]);
            int fine_location = ContextCompat.checkSelfPermission(context, PERMISSIONS_STORAGE[1]);
            return coarse_location == PackageManager.PERMISSION_GRANTED &&
                    fine_location == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static boolean checkNotificationPermission(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

}
