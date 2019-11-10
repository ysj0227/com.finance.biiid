package com.finance.commonlib.bottompopmenu;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Description:
 * Created by bruce on 2019/1/29.
 */
public class BottomPopMenu implements PopWindowInterface,
        PopWindowInterface.OnStartShowListener, PopWindowInterface.OnStartDismissListener {

    private Activity mActivity;

    private PopUpWindow mPopUpWindow;

    private View mControlView = null;
    private Animation mControlViewOpenAnimation;
    private Animation mControlViewCloseAnimation;
    private boolean mIsShowControlViewAnim;

    public BottomPopMenu(Activity activity, int titleResId, int messageResId) {
        this(activity, titleResId == 0 ? null : activity.getString(titleResId),
                messageResId == 0 ? null : activity.getString(messageResId));
    }

    public BottomPopMenu(Activity activity, CharSequence title, CharSequence message) {
        mActivity = activity;
        initPopWindow(activity, title, message);
    }

    public BottomPopMenu(Activity activity) {
        mActivity = activity;
        initPopWindow(activity, null, null);
    }

    private void initPopWindow(Activity activity, CharSequence title, CharSequence message) {
        mPopUpWindow = new PopUpWindow(activity, title, message, this);
    }

    @Override
    public void setView(View view) {
        if (view != null) {
            if (mPopUpWindow != null) {
                mPopUpWindow.setView(view);
            }
        }
    }

    @Override
    public void addContentView(View view) {
        if (mPopUpWindow != null) {
            mPopUpWindow.addContentView(view);
        }
    }

    @Override
    public void setIsShowLine(boolean isShowLine) {
        if (mPopUpWindow != null) {
            mPopUpWindow.setIsShowLine(isShowLine);
        }
    }

    @Override
    public void setIsShowCircleBackground(boolean isShow) {
        if (mPopUpWindow != null) {
            mPopUpWindow.setIsShowCircleBackground(isShow);
        }
    }

    @Override
    public void setPopWindowMargins(int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        if (mPopUpWindow != null) {
            mPopUpWindow.setPopWindowMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        }
    }

    @Override
    public void onStartDismiss(PopWindowInterface popWindowInterface) {
        if (mIsShowControlViewAnim) {
            mControlView.startAnimation(mControlViewCloseAnimation);
        }
    }

    @Override
    public void onStartShow(PopWindowInterface popWindowInterface) {
        if (mIsShowControlViewAnim) {
            mControlView.startAnimation(mControlViewOpenAnimation);
        }
    }

    /**
     * 设置控制控件的动画
     *
     * @param view       控制控件
     * @param openAnim   打开动画
     * @param closeAnim  关闭动画
     * @param isShowAnim 是否显示动画
     */
    public void setControlViewAnim(View view, Animation openAnim, Animation closeAnim, boolean isShowAnim) {
        mControlView = view;
        openAnim.setFillAfter(true);
        closeAnim.setFillAfter(true);
        mControlViewOpenAnimation = openAnim;
        mControlViewCloseAnimation = closeAnim;
        mIsShowControlViewAnim = isShowAnim;
    }

    /**
     * @param view        控制控件
     * @param openAnimId  打开动画id
     * @param closeAnimId 关闭动画id
     * @param isShowAnim  是否显示动画
     */
    public void setControlViewAnim(View view, int openAnimId, int closeAnimId, boolean isShowAnim) {
        Animation openAnim = AnimationUtils.loadAnimation(mActivity, openAnimId);
        Animation closeAnim = AnimationUtils.loadAnimation(mActivity, closeAnimId);
        setControlViewAnim(view, openAnim, closeAnim, isShowAnim);
    }

    public static class Builder {

        private Activity activity;
        private CharSequence title;
        private CharSequence message;
        private BottomPopMenu popWindow;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setTitle(int titleResId) {
            this.title = activity.getString(titleResId);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(int messageResId) {
            this.message = activity.getString(messageResId);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            this.message = message;
            return this;
        }

        public Builder setView(View view) {
            create().setView(view);
            return this;
        }

        public Builder addContentView(View view) {
            create().addContentView(view);
            return this;
        }

        public Builder setIsShowLine(boolean isShowLine) {
            create().setIsShowLine(isShowLine);
            return this;
        }

        public Builder setIsShowCircleBackground(boolean isShow) {
            create().setIsShowCircleBackground(isShow);
            return this;
        }

        public Builder addItemAction(PopItemAction popItemAction) {
            create().addItemAction(popItemAction);
            return this;
        }

        public Builder setPopWindowMargins(int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
            create().setPopWindowMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            return this;
        }

        public Builder setControlViewAnim(View view, Animation openAnim, Animation closeAnim, boolean isShowAnim) {
            create().setControlViewAnim(view, openAnim, closeAnim, isShowAnim);
            return this;
        }

        public Builder setControlViewAnim(View view, int openAnimId, int closeAnimId, boolean isShowAnim) {
            create().setControlViewAnim(view, openAnimId, closeAnimId, isShowAnim);
            return this;
        }

        public BottomPopMenu create() {
            if (popWindow == null) {
                popWindow = new BottomPopMenu(activity, title, message);
            }
            return popWindow;
        }

        public BottomPopMenu show(View view) {
            create();
            popWindow.show(view);
            return popWindow;
        }

        public BottomPopMenu show() {
            return show(null);
        }

    }

    public void show() {
        show(null);
    }

    public void show(View view) {
        if (mPopUpWindow != null) {
            mPopUpWindow.show();
        }
    }

    public void dismiss() {
        if (mPopUpWindow != null) {
            mPopUpWindow.dismiss();
        }
    }

    @Override
    public void addItemAction(PopItemAction popItemAction) {
        if (popItemAction == null) {
            return;
        }
        if (popItemAction.getTextResId() != 0) {
            popItemAction.setText(mActivity.getString(popItemAction.getTextResId()));
        }
        mPopUpWindow.addItemAction(popItemAction);
    }

}