package com.finance.biiid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.finance.commonlib.base.BaseActivity;
import com.finance.commonlib.utils.StatusBarUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;


@SuppressLint("Registered")
@EActivity(R.layout.activity_launch)
public class LaunchActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
        }
    }

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this);
        new Handler().postDelayed(this::gotoMainActivity, 2000);
    }

    private void gotoMainActivity() {
        MainActivity_.intent(this).start();
        finish();
    }
}
