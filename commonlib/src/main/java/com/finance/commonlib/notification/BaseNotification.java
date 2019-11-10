package com.finance.commonlib.notification;

import android.util.SparseArray;

import java.util.ArrayList;

public class BaseNotification {

    private static volatile BaseNotification Instance = null;

    private SparseArray<ArrayList<Object>> observers = new SparseArray<>();

    public static BaseNotification newInstance() {
        BaseNotification localInstance = Instance;
        if (localInstance == null) {
            synchronized (BaseNotification.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new BaseNotification();
                }
            }
        }
        return localInstance;
    }

    public void postNotificationName(int id, Object... args) {
        postNotificationNameInternal(id, false, args);
    }

    private void postNotificationNameInternal(int id, boolean allowDuringAnimation, Object... args) {
        ArrayList<Object> objects = observers.get(id);
        if (objects != null && !objects.isEmpty()) {
            int count = objects.size();
            for (int i = count - 1; i >= 0; i--) {
                Object obj = objects.get(i);
                ((NotificationCenterDelegate) obj).didReceivedNotification(id, args);
            }
        }
    }

    public void addObserver(Object observer, int id) {
        if (null == observers) return;

        ArrayList<Object> objects = observers.get(id);
        if (objects == null) {
            observers.put(id, (objects = new ArrayList<>()));
        }
        if (objects.contains(observer)) return;
        objects.clear();
        objects.add(observer);
    }

    /**
     * 粘性通知，所有注册统一id的都会收到此通知，除非remove掉
     */
    public void addStickObserver(Object observer, int id) {
        if (null == observers) return;

        ArrayList<Object> objects = observers.get(id);
        if (objects == null) {
            observers.put(id, (objects = new ArrayList<>()));
        }
        if (!objects.contains(observer)) {
            objects.add(observer);
        }
    }

    public void removeObserver(Object observer, int id) {
        ArrayList<Object> objects = observers.get(id);
        if (objects != null) {
            objects.remove(observer);
        }
    }

    public interface NotificationCenterDelegate {
        void didReceivedNotification(int id, Object... args);
    }

}
