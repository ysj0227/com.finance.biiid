package com.finance.commonlib.base;

/**
 * Description:mvp base activity
 * Created by bruce on 2019/1/10.
 */
public abstract class BaseMvpActivity<T extends BasePresenter>
        extends BaseActivity implements BaseView {

    protected T mPresenter;

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }

//    /**
//     * 绑定生命周期 防止MVP内存泄漏
//     *
//     * @param <T>
//     * @return
//     */
//    @Override
//    public <T> AutoDisposeConverter<T> bindAutoDispose() {
//        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider
//                .from(this, Lifecycle.Event.ON_DESTROY));
//    }
}
