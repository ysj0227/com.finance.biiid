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
import android.widget.Toast;

import com.finance.biiid.R;

public class ProtocolDialog {

    public static void dialog(Context context) {
        Dialog dialog = new Dialog(context, R.style.BottomDialog);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_protocol, null);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        TextView tvContent = inflate.findViewById(R.id.tv_content);
        String str = "协议，网络协议的简称，网络协议是通信计算机双方必须共同遵从的一组约定。如怎么样建立连接、怎么样互相识别等。只有遵守这个约定，计算机之间才能相互通信交流《用户隐私协议》和《用户服务协议》";

        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(str);
        // 设置字体大小
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(30);
        // 相对于默认字体大小的倍数,这里是1.3倍
        // RelativeSizeSpan sizeSpan1 = new RelativeSizeSpan((float) 1.3);
        spannableBuilder.setSpan(sizeSpan, 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 单独设置字体颜色
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FF3838"));
        spannableBuilder.setSpan(colorSpan, str.indexOf("《"), str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        // 单独设置点击事件
        ClickableSpan clickableSpanOne = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "拨打电话", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void updateDrawState(TextPaint paint) {
                //paint.setColor(Color.parseColor("#3072F6"));
                // 设置下划线 true显示、false不显示
                paint.setUnderlineText(false);
            }
        };
        spannableBuilder.setSpan(clickableSpanOne, str.indexOf("《"), str.indexOf("《")+8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


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
