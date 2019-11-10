package com.finance.commonlib.base;

/**
 * Description:
 * Created by bruce on 2019/1/10.
 */
public interface BaseView {

    /**
     * 显示加载中
     */
    void showLoadingDialog();

    void showLoadingDialog(final String text);

    /**
     * 隐藏加载
     */
    void hideLoadingDialog();

    /**
     * toast提示
     */
    void shortTip(int resId);

    void shortTip(String tip);

//    /**
//     * 数据获取失败
//     *
//     * @param throwable
//     */
//    void onError(Throwable throwable);

//    /**
//     * 绑定Android生命周期 防止RxJava内存泄漏
//     *
//     * @param <T>
//     * @return
//     */
//    <T> AutoDisposeConverter<T> bindAutoDispose();

}