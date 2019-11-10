package com.finance.commonlib.base;

import android.app.Activity;
import android.content.res.Resources;

import com.finance.commonlib.utils.CommonHelper;
import com.finance.commonlib.utils.log.LogCat;

import org.litepal.LitePalApplication;

import java.util.LinkedList;
import java.util.List;


public class BaseApplication extends LitePalApplication {
    private static BaseApplication instance = null;
    private List<Activity> activityList = new LinkedList<>();
    public static boolean isCheckedToken;//是否有网络启动

    public synchronized static BaseApplication getInstance() {
        if (instance == null) {
            instance = new BaseApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * 重写 getResource 方法，禁止app字体大小跟随系统字体大小调节
     */
    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        if (resources != null && resources.getConfiguration().fontScale != 1.0f) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = 1.0f;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resources;
    }

    public void addActivity(Activity activity) {
        if (!activityList.contains(activity))
            activityList.add(activity);
    }

    public void finishActivities() {
        for (Activity activity : activityList) {
            if (activity != null)
                activity.finish();
        }
        activityList.clear();
    }

    /**
     * 在每个activity的onCreate方法中调用addActivity方法，在应用程序退出时调用exit方法，就可以完全退出
     */
    public void quit() {
        try {
            finishActivities();
        } catch (Exception e) {
            LogCat.e(CommonHelper.getCName(new Exception()), e.getMessage(), e);
        } finally {
            System.exit(0);
        }
    }

}
