package com.finance.commonlib.utils.log;

import android.os.Environment;
import android.util.Log;

import com.finance.commonlib.utils.DateTimeUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class LogHelper {

    private static final String TAG = "LogHelper";

    private LogHelper() {
    }

    /**
     * 导出日志
     *
     * @param tag       类名称
     * @param message   错误消息
     * @param isSaveLog 是否保存到文件
     */
    public static void exportLog(String tag, String message, boolean isSaveLog) {
        if (isSaveLog)
            writeLog(tag, message);
        else
            printLog(tag, message);
    }

    /**
     * 输出正常日志
     *
     * @param className  类名称
     * @param methodName 方法名称
     * @param message    要输出的信息
     */
    public static void outLog(String className, String methodName, String message) {
        try {
            String strTag = className + "-->" + methodName + ":" + message;
            System.out.println(strTag);
        } catch (Exception e) {
            Log.e(TAG, "outLog, Exception:" + e.getMessage());
        }
    }

    private static void printLog(String tag, String message) {
        try {
            String strTag = tag + "-->";
            Log.e(strTag, message);
        } catch (Exception e) {
            Log.e(TAG, "printLog, Exception:" + e.getMessage());
        }
    }

    private static void writeLog(String tag, String message) {
        try {
            File filePath = null;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                filePath = new File(Environment.getExternalStorageDirectory() + "/Sunmi/logs/");
                writeFile(filePath, tag, message);
            }
        } catch (Exception e) {
            Log.e(TAG, "writeLog，Exception:" + e.getMessage());
        }
    }

    private static void writeFile(File filePath, String tag, String message) {
        try {
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            String strFileName = DateTimeUtils.getStringDateTimeByStringPattern(
                    DateTimeUtils.DateTimePattern.SHORT_DATETIME_1) + ".txt";
            File file = new File(filePath, strFileName);
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath(), file.exists());
            String strMsg = getMessage(tag, message);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(strMsg);
            bufferedWriter.close();
        } catch (Exception e) {
            Log.e(TAG, "writeFile，Exception:" + e.getMessage());
        }
    }

    private static String getMessage(String tag, String message) {
        String strMessage;
        String strTime = DateTimeUtils.getStringDateTimeByStringPattern(
                DateTimeUtils.DateTimePattern.LONG_DATETIME_1);
        StringBuffer builder = new StringBuffer();
        builder.append("TRACE__TIME:" + strTime);
        builder.append("  E/" + tag);
        builder.append(": " + message + "\n\r");
        strMessage = builder.toString();
        return strMessage;
    }

}
