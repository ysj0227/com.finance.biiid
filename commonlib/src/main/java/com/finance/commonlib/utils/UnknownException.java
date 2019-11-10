package com.finance.commonlib.utils;

import android.content.Context;

import com.finance.commonlib.base.BaseApplication;
import com.finance.commonlib.utils.log.LogCat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;


public class UnknownException implements UncaughtExceptionHandler {
    private static UnknownException instance;
    private Context mContext = null;
    private UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;
    private boolean isSaveLog;

    private UnknownException() {
    }

    public static UnknownException getInstance() {
        if (instance == null) {
            synchronized (UnknownException.class) {
                if (instance == null) {
                    instance = new UnknownException();
                }
            }
        }
        return instance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (!handleException(throwable) && mDefaultUncaughtExceptionHandler != null) {
            mDefaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
            }
        }
        BaseApplication.getInstance().quit();//捕获异常保存日志后直接闪退
    }

    /**
     * 捕获全局异常
     *
     * @param context 上下文
     */
    public void init(Context context, boolean isSaveLog) {
        this.mContext = context;
        this.isSaveLog = isSaveLog;

        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private boolean handleException(Throwable throwable) {
        if (throwable == null) {
            return true;
        }
        String msg = throwable.getLocalizedMessage();
        if (msg == null) {
            return false;
        }
        ToastUtils.toastForShort(mContext, "Sorry,app generates unknown error,please retry few minutes later");
        LogCat.e(CommonHelper.getCName(new Exception()), throwable);
        return true;
    }

    private String getErrorInfo(Throwable throwable) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        String errorInfo = writer.toString();
        return errorInfo;
    }

}

