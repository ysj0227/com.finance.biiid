package com.finance.biiid.previewimg;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * @author yangShiJie
 * @date 2019-11-14
 */
public class PageAdapter extends PagerAdapter {
    List<String> imagesUrl;
    Context context;
    private onLongClickListener longClickListener;

    public onLongClickListener getLongClickListener() {
        return longClickListener;
    }

    public void setLongClickListener(onLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public PageAdapter(List<String> imagesUrl, Context context) {
        this.imagesUrl = imagesUrl;
        this.context = context;
    }

    @Override
    public int getCount() {
        return (imagesUrl == null || imagesUrl.size() == 0) ? 0 : imagesUrl.size();
    }

    @NotNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String url = imagesUrl.get(position);
        PhotoView photoView = new PhotoView(context);
        Glide.with(context)
                .load(url)
                .into(photoView);
        container.addView(photoView);
        photoView.setOnLongClickListener(v -> {
            longClickListener.longItemClick(position);
            return false;
        });
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public interface onLongClickListener{
        void longItemClick(int position);
    }
}