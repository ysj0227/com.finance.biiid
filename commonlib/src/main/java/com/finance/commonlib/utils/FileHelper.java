package com.finance.commonlib.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.finance.commonlib.base.BaseApplication;

import java.io.File;


public class FileHelper {
    public static String APP_DATA_ROOT_PATH;
    public static String SDCARD_ROOT_PATH;
    public static String IMG_SAVE_PATH;//图片文件夹
    public static String CACHE_IMAGE_PATH;//图片缓存 未用
    public static String APK_INSTALL_PATH;
    public static String APK_LOG_PATH;
    public static String EXCEPTION_LOG_PATH;
    public static String DATA_PATH;
    public static String SDCARD_PATH;
    public static String SDCARD_CACHE_IMAGE_PATH;
    public static String SDCARD_CUT_PATH;
    public static String FILE_PATH;
    public static String updateApkPath;
    private static FileHelper mInstance;
    private Context mContext;

    public FileHelper(Context context) {
        mContext = context;
        initPath();
    }

    /**
     * 获取文件工具类实例
     *
     * @return FileUtils
     */
    public static FileHelper getInstance() {
        FileHelper fileHelper = mInstance;
        if (fileHelper == null) {
            synchronized (FileHelper.class) {
                fileHelper = mInstance;
                if (fileHelper == null) {
                    mInstance = fileHelper = new FileHelper(BaseApplication.getContext());
                }
            }
        }
        return fileHelper;
    }

    public File createDir(String path) {
        if (TextUtils.isEmpty(path)) return null;
        File file = new File(path);
        boolean hasFile = true;
        if (!file.exists()) {
            hasFile = file.mkdirs();
        }
        return hasFile ? file : null;
    }

    /**
     * 初始化本地缓存路径
     */
    private void initPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            SDCARD_ROOT_PATH = "/Sunmi/";
            SDCARD_PATH = Environment.getExternalStorageDirectory() + SDCARD_ROOT_PATH;   // sd卡 目录
            SDCARD_CACHE_IMAGE_PATH = SDCARD_PATH + "cache/image/";               // sd卡下图片文件缓存
            APK_INSTALL_PATH = SDCARD_PATH + "app/";                              // sd卡 更新包
            APK_LOG_PATH = SDCARD_PATH + "log/";                                  // sd卡 Log

            DATA_PATH = mContext.getFilesDir().getParent() + "/";                 // DATA
            APP_DATA_ROOT_PATH = DATA_PATH + "Sunmi/";                            // sunmi文件夹
            EXCEPTION_LOG_PATH = DATA_PATH + "logs/";                             // 异常log

            IMG_SAVE_PATH = APP_DATA_ROOT_PATH + "images/";                       // 图片文件夹
            CACHE_IMAGE_PATH = IMG_SAVE_PATH + "cache/";                          // 用于保存图片缓存吧

            FILE_PATH = SDCARD_PATH + "files/";                                   // 消息文件

            updateApkPath = APK_INSTALL_PATH + "update.apk";

            createDir(SDCARD_PATH);  // sd卡 sunmi 文件夹
            createDir(SDCARD_CUT_PATH);// sd卡 图片 二维码

            File file = new File(APP_DATA_ROOT_PATH);
            file.setExecutable(true, true);
            file.setReadable(true, true);
            file.setWritable(true, true);
            if (!file.exists()) {
                file.mkdirs();
            }
            createDir();
        }
    }

    /**
     * 没有权限时，外部路径会创建失败
     */
    public void createDir() {
        createDir(IMG_SAVE_PATH);   // 图片
        createDir(APK_LOG_PATH); // 异常保存路径
        createDir(FILE_PATH);
        createDir(SDCARD_CACHE_IMAGE_PATH);
    }

}
