package com.finance.biiid.previewimg;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.finance.biiid.R;
import com.finance.commonlib.base.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import androidx.viewpager.widget.ViewPager;

/**
 * @author yangShiJie
 * @date 2019-11-14
 */
@SuppressLint("Registered")
@EActivity(R.layout.activtiy_big_image)
public class ImageBigActivity extends BaseActivity {
    @ViewById(R.id.page)
    TextView page;
    @ViewById(R.id.top)
    RelativeLayout top;
    @ViewById(R.id.viewPager)
    PhotoViewPager viewPager;
    PageAdapter pagerAdapter;
    @Extra
    ArrayList<String> imagesUrl;
    @Extra
    int current;

    @AfterViews
    void init() {
        pagerAdapter = new PageAdapter(imagesUrl, getApplicationContext());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(current);
        page.setText((current + 1) + "/" + imagesUrl.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current = position;
                page.setText(current + 1 + "/" + imagesUrl.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
