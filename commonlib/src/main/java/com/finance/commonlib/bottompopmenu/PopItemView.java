package com.finance.commonlib.bottompopmenu;

import android.content.Context;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.finance.commonlib.R;

import androidx.appcompat.widget.AppCompatTextView;


/**
 * Created by HMY on 2016/9/10.
 */
public class PopItemView extends AppCompatTextView implements View.OnClickListener {

    private PopItemAction mPopItemAction;
    private BottomPopMenu mPopWindow;

    public PopItemView(Context context, PopItemAction popItemAction, BottomPopMenu windowController) {
        super(context);
        mPopItemAction = popItemAction;
        mPopWindow = windowController;

        int padding = getResources().getDimensionPixelOffset(R.dimen.pop_item_padding);
        setPadding(padding, padding, padding, padding);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (popItemAction != null) {
            if (popItemAction.getStyle() == PopItemAction.PopItemStyle.Normal) {
                setTextColor(getResources().getColor(R.color.text_main));
            } else if (popItemAction.getStyle() == PopItemAction.PopItemStyle.Cancel) {
                setTextColor(getResources().getColor(R.color.text_main));
                getPaint().setFakeBoldText(true);
            } else if (popItemAction.getStyle() == PopItemAction.PopItemStyle.Warning) {
                setTextColor(getResources().getColor(R.color.common_orange));
            }
        }
        setGravity(Gravity.CENTER);
        setClickable(true);
        setOnClickListener(this);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.sp_18));
        setText(popItemAction.getText());
    }

    @Override
    public void onClick(View view) {
        mPopItemAction.onClick();
        mPopWindow.dismiss();
    }

}
