package com.finance.biiid.previewimg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.finance.biiid.R;
import com.finance.biiid.config.AppConfig;
import com.finance.biiid.wxapi.WXEntryActivity;
import com.finance.commonlib.base.BaseActivity;
import com.finance.commonlib.utils.ImageUtils;
import com.finance.commonlib.utils.ThreadPool;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.finance.biiid.config.WxShareConfig.isInstallWechat;

/**
 * @author yangShiJie
 * @date 2019-11-14
 */
@SuppressLint("Registered")
@EActivity(R.layout.activtiy_big_image)
public class ImageBigActivity extends BaseActivity implements PageAdapter.onLongClickListener {
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
    private String imgUrl;
    /**
     * 读写权限
     */
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    /**
     * 请求状态码
     */
    private static int REQUEST_PERMISSION_CODE = 1;

    @AfterViews
    void init() {
        pagerAdapter = new PageAdapter(imagesUrl, getApplicationContext());
        pagerAdapter.setLongClickListener(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(current);
        page.setText(String.format(Locale.getDefault(), "%d/%d", current + 1, imagesUrl.size()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                imgUrl = imagesUrl.get(position);
            }

            @Override
            public void onPageSelected(int position) {
                current = position;
                page.setText(String.format(Locale.getDefault(), "%d/%d", current + 1, imagesUrl.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Click(R.id.iv_more)
    void moreClick() {
        showDialog(this);
    }

    @Click(R.id.iv_back)
    void backClick() {
        finish();
    }

    //分享 授权登录
    private void gotoWxActivity(String imgUrl) {
        if (!isInstallWechat(context)) {
            shortTip(R.string.str_need_install_wx);
            return;
        }
        if (!TextUtils.isEmpty(imgUrl)){
            Intent intent = new Intent(this, WXEntryActivity.class);
            intent.putExtra(AppConfig.WX_TYPE, AppConfig.WX_TYPE_SEND_IMG);
            intent.putExtra(AppConfig.WX_DATA, imgUrl);
            startActivity(intent);
        }
    }

    private void saveAlbum() {
//        if (!TextUtils.isEmpty(imgUrl)){
//            Log.d(TAG,"11111 imgurl="+imgUrl);
//            return;
//        }
        ThreadPool.getCachedThreadPool ().execute(() -> {
            try {
                Bitmap bitmap = Glide.with(context)
                        .asBitmap()
                        .load(imgUrl)
                        .submit()
                        .get();
                if (ImageUtils.saveImageToGallery(context, bitmap, 90)) {
                    shortTip(R.string.save_success);
                } else {
                    shortTip(R.string.save_fail);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 检测是否需要读写权限
     */
    private void checkRequestPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            } else {
                saveAlbum();
            }
        }
    }

    /**
     * 获取权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveAlbum();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThreadPool.getSingleThreadPool().shutdownNow();
    }

    @Override
    public void longItemClick(int position) {
        showDialog(this);
    }

    public void showDialog(final Context getActivity) {
        Dialog dialog = new Dialog(getActivity, R.style.BottomDialog);
        View inflate = LayoutInflater.from(getActivity).inflate(R.layout.dialog_ddq, null);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        if (dialogWindow == null) {
            return;
        }
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        // 屏幕宽度（像素）
        WindowManager wm = (WindowManager) getActivity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        //int height = dm.heightPixels;
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = width;
        dialogWindow.setAttributes(lp);

        TextView textUp = inflate.findViewById(R.id.tvUp);
        TextView textDown = inflate.findViewById(R.id.tvDown);
        TextView textCancel = inflate.findViewById(R.id.tvCancel);
        textUp.setText(R.string.save_text);
        textDown.setText(getString(R.string.wx_share_session));
        //on click
        View.OnClickListener listener = v -> {
            int i = v.getId();
            if (i == R.id.tvUp) {
                checkRequestPermissions();
            } else if (i == R.id.tvDown) {
                gotoWxActivity(imgUrl);
            }
            dialog.dismiss();
        };
        textUp.setOnClickListener(listener);
        textDown.setOnClickListener(listener);
        textCancel.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.show();//显示对话框
    }


}
