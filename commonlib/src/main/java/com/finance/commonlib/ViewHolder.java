package com.finance.commonlib;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 自定义通用ViewHolder
 * Created by shijie.yang on 2017/9/21.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;
    private Context mContext;

    /**
     * @param context
     * @param itemView
     * @param parent
     */
    public ViewHolder(Context context, View itemView, ViewGroup parent) {
        super(itemView);
        mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<View>();
    }

    /**
     * @param context
     * @param parent
     * @param layoutId
     * @return
     */
    public static ViewHolder get(Context context, ViewGroup parent, int layoutId) {

        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder holder = new ViewHolder(context, itemView, parent);
        return holder;
    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置文本显示
     *
     * @param viewId id
     * @param text   显示文本
     * @return
     */
    public ViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 设置图片显示
     *
     * @param viewId id
     * @param resId  显示的图片
     * @return
     */
    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    /**
     * 点击事件
     *
     * @param viewId   id
     * @param listener 监听listener
     * @return
     */
    public ViewHolder setOnClickListener(int viewId,
                                         View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

//调用例子
//    方法一:
//                TextView tv = holder.getView(R.id.name);
//                ImageView title = holder.getView(R.id.title);
//                tv.setText(s.getName());
//                title.setImageResource(s.getImageId());
//    方法二：
//                holder.setText(R.id.name,s.getName());
//                holder.setImageResource(R.id.title,s.getImageId());
//                holder.setOnClickListener(R.id.comItem,new View.OnClickListener()
//                  {
//                   @Override
//                    public void onClick (View v){
//                    Log.i("TAG", "aa" + s.getName());
//                   }
//                 });
}
