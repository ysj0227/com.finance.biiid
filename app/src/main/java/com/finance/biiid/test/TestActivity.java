package com.finance.biiid.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.finance.biiid.MyApplication;
import com.finance.biiid.R;
import com.finance.biiid.wxapi.WXEntryActivity;
import com.finance.biiid.wxapi.WXPayEntryActivity;
import com.tencent.mm.opensdk.constants.Build;


public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);
        Button button5 = findViewById(R.id.button5);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(TestActivity.this, WXEntryActivity.class);
            startActivity(intent);
        });
        button2.setOnClickListener(v -> {

        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this, WXPayEntryActivity.class);
                startActivity(intent);
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TestActivity.this, WXEntryActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 是否支持微信支付
     */
    private void checkPaySupport(){
        boolean isPaySupported = MyApplication.WXapi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        Toast.makeText(TestActivity.this, String.valueOf(isPaySupported), Toast.LENGTH_SHORT).show();
    }


    private void aa(){
    }

}
