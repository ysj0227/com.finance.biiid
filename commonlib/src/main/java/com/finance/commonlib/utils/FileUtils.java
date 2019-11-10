package com.finance.commonlib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.finance.commonlib.base.BaseApplication;
import com.finance.commonlib.utils.log.LogCat;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;



import static javax.xml.transform.OutputKeys.ENCODING;

/**
 * 文件工具类
 */
public class FileUtils {
    private final static String TAG = "FileUtils";
    private static final int SIZE_TYPE_B = 1;// 获取文件大小单位为B的double值
    private static final int SIZE_TYPE_KB = 2;// 获取文件大小单位为KB的double值
    private static final int SIZE_TYPE_MB = 3;// 获取文件大小单位为MB的double值
    private static final int SIZE_TYPE_GB = 4;// 获取文件大小单位为GB的double值

    /**
     * 删除dir目录和目录下的所有文件
     */
    private static boolean delDir(File dir) {
        if (dir == null || !dir.exists() || dir.isFile()) {
            return false;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                delDir(file);// 递归
            }
        }
        dir.delete();
        return true;
    }

    /**
     * 删除dir目录下的所有文件
     */
    public static boolean delAllFile(File dir) {
        if (dir == null || !dir.exists() || dir.isFile()) {
            return false;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                delDir(file);// 递归
            }
        }
        return true;
    }

    /**
     * 返回某个目录文件下文件总大小 kb
     */
    public static double getFolderSize(String filepath) {
        File file = new File(filepath);
        double size = 0;
        File[] fileList = file.listFiles();
        for (int i = 0; fileList != null && i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i].getPath());
            } else {
                size = size + fileList[i].length();
            }
        }
        return size / 1024;
    }

    /**
     * get all files in the specified path
     */
    public static List<String> getFileListByPath(String path) {
        List<String> items = new ArrayList<String>();
        try {
            File f = new File(path);
            File[] fileList = f.listFiles();// 列出所有文件
            if (fileList != null) {
                for (File file : fileList) {
                    items.add(file.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * get a file name from the whole path
     */
    public static String getFileNameFromPath(String path) {
        if (path == null) return "";
        String fileName = path;
        int start = path.lastIndexOf("/");
        int end = path.length();
        if (start != -1 && end != -1) {
            fileName = path.substring(start + 1, end);
        }
        return fileName == null ? "" : fileName;
    }

    /**
     * 文件大小格式化展示
     */
    public static String formatFileSize(long length) {
        String result = null;
//        long bytes = length / 8;
        if (length >> 20 > 1) {  //M为单位
            result = String.valueOf(length >> 20) + "MB";
        } else {   //KB为单位
            result = String.valueOf(length >> 10) + "KB";
        }
        return result;
    }

    /**
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return FormatFileSize(blockSize, sizeType);
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static long getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return blockSize;
    }

    /**
     * 获取指定文件大小
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹大小
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File fList[] = f.listFiles();
        for (File file : fList) {
            if (file.isDirectory()) {
                size = size + getFileSizes(file);
            } else {
                size = size + getFileSize(file);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     */

    public static String FormatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        String wrongSize = "0.00B";
        if (fileS == 0) return wrongSize;

        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     */
    private static double FormatFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZE_TYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZE_TYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZE_TYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZE_TYPE_GB:
                fileSizeLong = Double.valueOf(df
                        .format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    public static String readLocalFile(String path) {
        String result = "";

        File file = new File(path);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String readLine;
            StringBuilder sb = new StringBuilder();

            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            result = sb.toString();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static InputStream byte2Input(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    /**
     * 根据文件名获取文件类型名称
     */
    public static String getFileTypeName(String name) {
        return name.substring(name.lastIndexOf(".") + 1, name.length());
    }

    public static String getBoardThumbPath(String path) {
        String type = getFileTypeName(path);
        if (TextUtils.equals(type, "gif")) {
            return path; //gif直接返回原图
        }
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        String filePath = path.replace(fileName, "");
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        fileName += "_thumbnail.jpg";
        return filePath + fileName;
    }

    /**
     * 计算文件的MD5码
     *
     * @param file
     * @return
     */
    public static String getMD5(File file) {
        FileInputStream fis = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length = -1;
            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            byte md5Data[] = md.digest();
            String str = "";
            for (byte bData : md5Data) {
                int b = (0xFF & bData);
                if (b <= 0xF) str += "0";
                str += Integer.toHexString(b);
            }
            return str;

        } catch (IOException ex) {
            return null;
        } catch (NoSuchAlgorithmException ex) {
            return null;
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readAssertHtml(Context context, String fileName) {
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            int length = inputStream.available();
            byte[] contents = new byte[length];
            inputStream.read(contents, 0, length);
            return new String(contents, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    //从assets 文件夹中获取文件并读取数据
    public static String getStringFromAssets(Context context, String fileName) {
        String result = "";
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            //获取文件的字节数
            int length = in.available();
            //创建byte数组
            byte[] buffer = new byte[length];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            result = EncodingUtils.getString(buffer, ENCODING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断SD卡是否存在
     *
     * @return true-存在；false-不存在
     */
    public static boolean isSDExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径（不带/）
     *
     * @return 返回SD卡路径
     */
    public static String getSDPath() {
        String sdPath = "";

        File sdDir = null;
        if (isSDExist()) {
            sdDir = Environment.getExternalStorageDirectory();
        }

        if (sdDir != null) {
            sdPath = sdDir.getAbsolutePath();
        }

        return sdPath;
    }


    //让相册检测不到此路径下图片视频
    public boolean nomediaPath(String nomediaPath) {
        File mFile = new File(nomediaPath + ".nomedia");
        if (mFile.exists()) {
            return true;
        }
        try {
            return mFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将html文件保存到sd卡
     */
    public void saveFile(String content, String path) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
            bw.write(content);
        } catch (IOException ioe) {
            Log.e(TAG, "saveFile error!", ioe);
        } finally {
            try {
                if (bw != null) bw.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                Log.e(TAG, "saveFile error!", e);
            }
        }
    }

    /**
     * 将文件保存到SDCard
     */
    public boolean saveFile2SDCard(String fileName, byte[] dataBytes) throws IOException {
        boolean flag = false;
        FileOutputStream fs = null;
        try {
            if (!TextUtils.isEmpty(fileName)) {
                File file = newFileWithPath(fileName);
                if (file.exists()) {
                    file.delete();
                }
                fs = new FileOutputStream(file);
                fs.write(dataBytes, 0, dataBytes.length);
                fs.flush();
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fs != null) fs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 判断文件是否存在
     */
    public boolean isExists(String strPath) {
        if (TextUtils.isEmpty(strPath)) return false;

        final File strFile = new File(strPath);
        return strFile.exists();
    }

    /**
     * 创建一个文件，如果其所在目录不存在时，他的目录也会被跟着创建
     */
    public File newFileWithPath(String filePath) {
        if (TextUtils.isEmpty(filePath)) return null;

        int index = filePath.lastIndexOf(File.separator);

        String path;
        if (index != -1) {
            path = filePath.substring(0, index);
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                if (!file.exists() && !file.isDirectory()) {// 如果文件夹不存在
                    boolean flag = file.mkdirs();
                }
            }
        }
        return new File(filePath);
    }

    /**
     * 拷贝一个文件到另一个目录
     */
    public boolean copyFile(String from, String to) {
        File fromFile, toFile;
        fromFile = new File(from);
        toFile = new File(to);
        FileInputStream fis;
        FileOutputStream fos;

        try {
            toFile.createNewFile();
            fis = new FileInputStream(fromFile);
            fos = new FileOutputStream(toFile);
            int bytesRead;
            byte[] buf = new byte[4 * 1024];  // 4K buffer
            while ((bytesRead = fis.read(buf)) != -1) {
                fos.write(buf, 0, bytesRead);
            }
            fos.flush();
            fos.close();
            fis.close();
        } catch (IOException e) {
            LogCat.e(TAG, e);
            return false;
        }
        return true;
    }

    public boolean copyFileAndNotifyAlbum(String from, String to) {
        boolean result = copyFile(from, to);
        if (result) {
            BaseApplication.getInstance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + to)));
        }
        return result;
    }

    public boolean deleteFile(String strPath) {
        if (TextUtils.isEmpty(strPath)) return false;

        final File strFile = new File(strPath);
        return strFile.exists() && strFile.delete();
    }

    public String readContent(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        String result = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = new FileInputStream(file);
            byte[] bytes = new byte[2048];
            bos = new ByteArrayOutputStream();

            int length = -1;

            while ((length = fis.read(bytes)) != -1) {
                bos.write(bytes, 0, length);
            }
            byte[] var5 = bos.toByteArray();
            result = new String(var5);

            bos.close();
            fis.close();
        } catch (Exception e) {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 获取apk文件的版本号
     */
    public int getApkVersion(Context context, String updateApkPath) {
        PackageInfo info = context.getPackageManager()
                .getPackageArchiveInfo(updateApkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            String packageName = appInfo.packageName;  //得到安装包名称
            int versionCode = info.versionCode;       //得到版本信息
            return packageName.equals(context.getPackageName()) ? versionCode : 0;
        } else
            return 0;
    }

    /**
     * 获取apk文件的版本名称
     */
    public String getApkVersionName(Context context, String updateApkPath) {
        PackageInfo info = context.getPackageManager().getPackageArchiveInfo(updateApkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            String packageName = appInfo.packageName;  //得到安装包名称
            String versionName = info.versionName;       //得到版本信息
            return packageName.equals(context.getPackageName()) ? versionName : "";
        } else
            return "";
    }

    /**
     * 获取单个文件的MD5值！
     */
    public String getFileMD5(String filePath) {
        File file = new File(filePath);
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream fis;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            while ((len = fis.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 读取手机存储空间中的文件
     *
     * @param context  上下文
     * @param fileName 文件全路径
     * @return 返回读取到的内容
     */
    public static String readFile(Context context, String fileName) {
        String result = "";
        FileInputStream mFileInputStream = null;
        try {
            mFileInputStream = context.openFileInput(fileName);
            int lenght = mFileInputStream.available();
            byte[] buffer = new byte[lenght];
            mFileInputStream.read(buffer);
            result = EncodingUtils.getString(buffer, "UTF-8");
        } catch (Exception e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
        } finally {
            if (mFileInputStream != null) {
                try {
                    mFileInputStream.close();
                } catch (IOException e) {
                    LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
                }
            }
        }

        return result;
    }

    /**
     * 删除手机存储空间中文件
     *
     * @param context  上下文
     * @param fileName 文件全路径
     * @return 返回删除结果：删除成功-true；删除失败-false
     */
    public static boolean delFile(Context context, String fileName) {
        boolean result = false;

        try {
            result = context.deleteFile(fileName);
        } catch (Exception e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
        }

        return result;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     *
     * @param fileName 文件名称
     * @param path     路径
     * @return true-存在；false-不存在
     */
    public static boolean isFileExist(String fileName, String path) {
        File file = new File(FileUtils.getSDPath() + File.separator + path + File.separator + fileName);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入SD卡中
     *
     * @param path     文件路径
     * @param fileName 文件名称
     * @param input    输入流
     * @return 返回文件
     */
    public static File writeSDFromInput(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            createSDDir(path);
            file = creatFileInSDCard(fileName, path);
            output = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            int i;
            while (-1 != (i = input.read(buffer))) {
                output.write(buffer, 0, i);
            }
            output.flush();
        } catch (Exception e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
            }
        }
        return file;
    }

    /**
     * 将一个InputStream里面的数据写入手机默认路径中
     *
     * @param filename 文件名称
     * @param input    输入流
     * @param context  上下文
     */
    public static void writeSDFromInput(String filename, InputStream input, Context context) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            byte buffer[] = new byte[4 * 1024];
            int i;
            while (-1 != (i = input.read(buffer))) {
                fos.write(buffer, 0, i);
            }
            fos.flush();
        } catch (Exception e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
            }
        }
    }

    /**
     * 将字符串写到文件中并保存文件到SD卡的指定文件夹中
     *
     * @param fileFolder  指定文件夹
     * @param fileName    文件名
     * @param fileContent 文件内容
     */
    @SuppressLint("LongLogTag")
    public static void writeFileToSD(String fileFolder, String fileName, String fileContent) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            File filePath = null;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                filePath = new File(FileUtils.getSDPath() + File.separator + fileFolder);
                if (!filePath.exists()) {
                    filePath.mkdirs();
                }

                File file = new File(filePath, fileName);

                fileWriter = new FileWriter(file.getAbsolutePath());
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(fileContent);
                bufferedWriter.close();
            }
        } catch (IOException e) {
            Log.e("LogHelper-->writeFileToSD-->", "IOException:" + e.getMessage().toString());

        } catch (Exception e) {
            Log.e("LogHelper-->writeFileToSD-->", "Exception:" + e.getMessage());
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                Log.e("LogHelper-->writeFileToSD-->", "Exception:" + e.getMessage());
            }
        }
    }

    /**
     * 读取SD卡上的txt文件
     *
     * @param fileFullName 包含文件路径的文件名称
     * @return 返回文件内容
     */
    public static String readSDTxt(String fileFullName, String textEncoding) {
        String result = "";
        InputStream inputStream = null;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                inputStream = new FileInputStream(new File(FileUtils.getSDPath() + File.separator + fileFullName));
                if (inputStream != null) {
                    int length = inputStream.available();
                    byte[] buffer = new byte[length];
                    inputStream.read(buffer);

                    result = EncodingUtils.getString(buffer, textEncoding);
                    inputStream.close();
                }
            }
        } catch (Exception e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
                }
            }
        }
        return result;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dir 目录名称
     * @return 返回创建的目录
     */
    public static File createSDDir(String dir) {
        File dirFile = new File(FileUtils.getSDPath() + File.separator + dir);
        dirFile.mkdirs();
        return dirFile;
    }

    public static InputStream getInputStreamOfAssets(Context context, String fileName) {
        InputStream inputStream = null;

        try {
            inputStream = context.getAssets().open(fileName);
        } catch (IOException e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
        }

        return inputStream;
    }

    public static String readAllText(InputStream in, String encoding) throws IOException {
        String line = null;
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in, encoding));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } finally {
            if (reader != null) reader.close();
        }
    }

    /**
     * 在SD卡上创建文件
     *
     * @param fileName 文件名称
     * @param dir      目录名称
     * @return 返回创建的文件
     */
    public static File creatFileInSDCard(String fileName, String dir) {
        File file = null;
        try {
            file = new File(FileUtils.getSDPath() + dir + File.separator + fileName);
            file.createNewFile();
        } catch (IOException e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
        }
        return file;
    }

}
