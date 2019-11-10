package com.finance.commonlib.base;

/**
 * Description:
 * Created by bruce on 2019/1/10.
 */
public abstract class BaseMvpFragment<T extends BasePresenter>
        extends BaseFragment implements BaseView {

    protected T mPresenter;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroyView();
    }

}
