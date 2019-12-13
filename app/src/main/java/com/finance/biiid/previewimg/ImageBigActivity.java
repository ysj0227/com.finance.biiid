package com.finance.biiid.previewimg;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.finance.biiid.R;
import com.finance.biiid.config.AppConfig;
import com.finance.biiid.wxapi.WXEntryActivity;
import com.finance.commonlib.base.BaseActivity;
import com.finance.commonlib.utils.ImageUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private String imgUrl;
    private ExecutorService singleThreadPool;

    @AfterViews
    void init() {
        pagerAdapter = new PageAdapter(imagesUrl, getApplicationContext());
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
                saveAlbum();
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


    //分享 授权登录
    private void gotoWxActivity(String imgUrl) {
        if (!isInstallWechat()) {
            shortTip(R.string.str_need_install_wx);
            return;
        }
        Intent intent = new Intent(this, WXEntryActivity.class);
        intent.putExtra(AppConfig.WX_TYPE, AppConfig.WX_TYPE_SEND_IMG);
        intent.putExtra(AppConfig.WX_DATA, imgUrl);
        startActivity(intent);
    }

    /**
     * 是否安装微信
     *
     * @return
     */
    private boolean isInstallWechat() {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void saveAlbum() {
        Log.d(TAG,"11111 imgUrl="+imgUrl);
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build();
        if (singleThreadPool == null) {
            singleThreadPool = new ThreadPoolExecutor(1, 3, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
            singleThreadPool.execute(() -> {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (singleThreadPool != null) {
            singleThreadPool.shutdownNow();
        }
    }
}
