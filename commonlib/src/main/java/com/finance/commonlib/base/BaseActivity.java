package com.finance.commonlib.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;

import com.finance.commonlib.notification.BaseNotification;
import com.finance.commonlib.utils.LoadingDialog;
import com.finance.commonlib.utils.ToastUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import butterknife.ButterKnife;


/**
 * 所有Activity的基类，其中包含组件初始化、activity之间跳转以及读写文件等方法。
 *
 * @author shijie.yang
 */
public abstract class BaseActivity extends FragmentActivity
        implements BaseNotification.NotificationCenterDelegate {
    protected final String TAG = this.getClass().getSimpleName();
    private long lastClickTime;
    protected Context context;
    protected LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!needLandscape()) {//默认竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        context = this;
        addObserver();
        if (activityLayoutId() > 0) {
            setContentView(activityLayoutId());
            ButterKnife.bind(this);
        }
        initDialog();
        initView();
        BaseApplication.getInstance().addActivity(this);
    }

    protected int activityLayoutId() {
        return 0;
    }

    protected void initView() {

    }

    /**
     * 子activity 通过override来设置是否允许横竖屏切换
     */
    protected boolean needLandscape() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();    //防止窗口泄漏
        loadingDialog = null;
        removeObserver();
    }

    @Override
    public Resources getResources() {//禁止app字体大小跟随系统字体大小调节
        Resources resources = super.getResources();
        if (resources != null && resources.getConfiguration().fontScale != 1.0f) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = 1.0f;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resources;
    }

    /**
     * 根据资源ID获取资源的字符串值
     *
     * @param resId 资源ID
     * @return 返回获取到的值
     */
    protected String getStringById(int resId) {
        return this.getResources().getString(resId);
    }

    /**
     * 短提示
     *
     * @param msg 提示语
     */
    public void shortTip(final String msg) {
        ToastUtils.toastForShort(context, msg);
    }

    /**
     * 短提示
     *
     * @param resId 本地资源id
     */
    public void shortTip(final int resId) {
        ToastUtils.toastForShort(context, resId);
    }

    private void initDialog() {
        if (loadingDialog == null) {
            synchronized (this) {
                if (loadingDialog == null) {
                    loadingDialog = new LoadingDialog(this);
                    loadingDialog.setCanceledOnTouchOutside(false);
                }
            }
        }
    }

    /**
     * 显示加载框
     */
    public void showLoadingDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingDialog == null || loadingDialog.isShowing()) {
                    return;
                }
                loadingDialog.setLoadingContent(null);
                loadingDialog.show();
            }
        });
    }

    /**
     * 显示加载框,content为null是不可点击消失
     */
    public void showLoadingDialog(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingDialog == null || loadingDialog.isShowing()) {
                    return;
                }
                if (!TextUtils.isEmpty(text))
                    loadingDialog.setLoadingContent(text);
                loadingDialog.show();
            }
        });
    }

    /**
     * 显示加载框,content为null是不可点击消失
     */
    public void showLoadingDialog(final String text, final int textColor) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingDialog == null || loadingDialog.isShowing()) {
                    return;
                }
                loadingDialog.setCancelable(false);
                if (!TextUtils.isEmpty(text))
                    loadingDialog.setTipColorText(text, textColor);
                loadingDialog.show();
            }
        });
    }

    /**
     * 关闭加载框
     */
    public void hideLoadingDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingDialog == null) {
                    return;
                }
                loadingDialog.setCancelable(true);
                loadingDialog.dismiss();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideLoadingDialog();
    }

    /**
     * 防止多次点击事件处理
     */
    public boolean isFastClick(int intervalTime) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < intervalTime) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    // ==============================================
    // ========== Activity redirect ===============
    // ==============================================

    /**
     * Activity之间的跳转
     *
     * @param context          当前上下文
     * @param cls              要跳转到的Activity类
     * @param isActivityFinish 是否销毁当前的Activity
     */
    protected void openActivity(Context context, Class<?> cls, boolean isActivityFinish) {
        Intent intent = new Intent(context, cls);
        startActivity(intent);
        if (isActivityFinish)
            this.finish();
    }

    /**
     * Activity之间的跳转
     *
     * @param context          当前上下文
     * @param cls              要跳转到的Activity类
     * @param bundle           跳转时传递的参数
     * @param isActivityFinish 是否销毁当前的Activity
     */
    protected void openActivity(Context context, Class<?> cls, Bundle bundle, boolean isActivityFinish) {
        Intent intent = new Intent(context, cls);
        intent.putExtras(bundle);
        startActivity(intent);
        if (isActivityFinish)
            this.finish();
    }

    /**
     * Activity之间获取结果的跳转
     *
     * @param context     当前上下文
     * @param cls         要跳转到的Activity类
     * @param requestCode 获取结果时的请求码
     */
    protected void openActivityForResult(Context context, Class<?> cls, int requestCode) {
        openActivityForResult(context, cls, requestCode, null);
    }

    /**
     * Activity之间获取结果的跳转（需要API16版本以上才可以使用）
     *
     * @param context     当前上下文
     * @param cls         要跳转到的Activity类
     * @param requestCode 获取结果时的请求码
     * @param bundle      跳转时传递的参数
     */
    protected void openActivityForResult(Context context, Class<?> cls, int requestCode, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }

    // ============================= 通知相关 begin=============================

    @Override
    public void didReceivedNotification(int id, Object... args) {

    }

    /**
     * 返回需要注册的通知的ID，默认为null，注册但不独占该通知
     * {@link BaseNotification#addStickObserver(Object, int)}
     */
    public int[] getStickNotificationId() {
        return null;
    }

    /**
     * 返回需要注册的通知的ID，默认为null，注册并独占该通知
     * {@link BaseNotification#addObserver(Object, int)} (Object, int)}
     */
    public int[] getUnStickNotificationId() {
        return null;
    }

    /**
     * 注册通知，该方法为public，允许根据需求手动调用
     */
    public final void addObserver() {
        int[] notificationIds = getUnStickNotificationId();
        if (notificationIds != null) {
            for (int notificationId : notificationIds) {
                BaseNotification.newInstance().addObserver(this, notificationId);
            }
        }

        notificationIds = getStickNotificationId();
        if (notificationIds != null) {
            for (int notificationId : notificationIds) {
                BaseNotification.newInstance().addStickObserver(this, notificationId);
            }
        }
    }

    /**
     * 反注册通知
     */
    public final void removeObserver() {
        // 1. 反注册
        int[] notificationIds = getUnStickNotificationId();
        if (notificationIds != null) {
            for (int notificationId : notificationIds) {
                BaseNotification.newInstance().removeObserver(this, notificationId);
            }
        }

        // 2. 反注册
        notificationIds = getStickNotificationId();
        if (notificationIds != null) {
            for (int notificationId : notificationIds) {
                BaseNotification.newInstance().removeObserver(this, notificationId);
            }
        }
    }

    // ============================= 通知相关 end=============================

}
