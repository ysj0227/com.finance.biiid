package com.finance.biiid.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.finance.biiid.ProtocolActivity;
import com.finance.biiid.ProtocolActivity_;
import com.finance.biiid.R;

public class ProtocolDialog {

    public static void dialog(Context context) {
        Dialog dialog = new Dialog(context, R.style.BottomDialog);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_protocol, null);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        TextView tvContent = inflate.findViewById(R.id.tv_content);
        String str =context.getString(R.string.str_protocol);

        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(str);
        // 设置字体大小
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(30);
        // 相对于默认字体大小的倍数,这里是1.3倍
        // RelativeSizeSpan sizeSpan1 = new RelativeSizeSpan((float) 1.3);
        spannableBuilder.setSpan(sizeSpan, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 单独设置字体颜色
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#CCFF6000"));//#FF3838
        spannableBuilder.setSpan(colorSpan, str.indexOf("《"), str.lastIndexOf("》")+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        // 单独设置点击事件
        ClickableSpan clickableSpanOne = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                ProtocolActivity_.intent(context).protocolType(ProtocolActivity.USER_PRIVATE).start();
            }
            @Override
            public void updateDrawState(TextPaint paint) {
                //paint.setColor(Color.parseColor("#3072F6"));
                // 设置下划线 true显示、false不显示
                paint.setUnderlineText(false);

            }
        };
        ClickableSpan clickableSpanTwo = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                ProtocolActivity_.intent(context).protocolType(ProtocolActivity.USER_PROTOCOL).start();
            }
            @Override
            public void updateDrawState(TextPaint paint) {
                //paint.setColor(Color.parseColor("#3072F6"));
                // 设置下划线 true显示、false不显示
                paint.setUnderlineText(false);
            }
        };
        spannableBuilder.setSpan(clickableSpanOne, str.indexOf("《"), str.indexOf("《")+7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableBuilder.setSpan(clickableSpanTwo, str.lastIndexOf("《"), str.lastIndexOf("》")+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvContent.setText(spannableBuilder);
        tvContent.setHighlightColor(Color.parseColor("#00000000"));

        inflate.findViewById(R.id.tv_confirm).setOnClickListener(v -> {
            SpUtils.saveProtocol();
            dialog.dismiss();
        });
        inflate.findViewById(R.id.tv_cancel).setOnClickListener(v -> {
            System.exit(0);
            System.gc();
        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();//显示对话框
    }


}
