package com.finance.commonlib.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池
 */
public class ThreadPool {

    static ExecutorService cachedService = null;
    static ExecutorService singleService = null;

    /**
     * @Description 不固定线程池
     */
    public static ExecutorService getCachedThreadPool() {
        if (cachedService == null) {
            synchronized (ThreadPool.class) {
                if (cachedService == null)
                    cachedService = Executors.newCachedThreadPool();
            }
        }
        return cachedService;
    }

    /**
     * @Description 单线程池
     */
    public static ExecutorService getSingleThreadPool() {
        if (singleService == null) {
            synchronized (ThreadPool.class) {
                if (singleService == null)
                    singleService = Executors.newSingleThreadExecutor();
            }
        }
        return singleService;
    }

}
