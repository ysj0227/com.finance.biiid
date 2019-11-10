package com.finance.commonlib.bottompopmenu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.finance.commonlib.R;


/**
 * Description:
 * Created by bruce on 2019/1/29.
 */
public class PopLineView extends View {

    public static final String TAG_LINE_VIEW = "tag_line_view";

    public PopLineView(Context context) {
        super(context);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.pop_line_height)));
        setBackgroundResource(R.color.text_disable);
        setTag(TAG_LINE_VIEW);
    }
}
