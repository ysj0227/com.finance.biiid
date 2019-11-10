package com.finance.commonlib.bottompopmenu;

import android.view.View;

/**
 * Description:
 * Created by bruce on 2019/1/29.
 */
public interface PopWindowInterface {

    void setView(View view);

    void addContentView(View view);

    void addItemAction(PopItemAction popItemAction);

    void setIsShowLine(boolean isShowLine);

    void setIsShowCircleBackground(boolean isShow);

    void setPopWindowMargins(int leftMargin, int topMargin, int rightMargin, int bottomMargin);

    interface OnStartShowListener {
        void onStartShow(PopWindowInterface popWindowInterface);
    }

    interface OnStartDismissListener {
        void onStartDismiss(PopWindowInterface popWindowInterface);
    }
}
