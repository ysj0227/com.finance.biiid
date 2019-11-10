package com.finance.commonlib.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class AppBaseFragment extends BaseFragment {
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(activityLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, v);//注解
        initView(inflater, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected abstract int activityLayoutId();

    protected abstract void initView(LayoutInflater inflater, View v);

}
