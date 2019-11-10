package com.finance.commonlib;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by shijie.yang on 2017/9/21.
 */
public abstract class CommonListAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context mContext;
    private int mLayoutId;
    private List<T> mList;
    private LayoutInflater mInflater;

    /**
     * @param context  上下文
     * @param layoutId layout
     * @param list     列表数据
     */
    public CommonListAdapter(Context context, int layoutId, List<T> list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mList = list;
    }

    public List<T> getData() {
        return mList;
    }

    public void setData(List<T> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        return ViewHolder.get(mContext, parent, mLayoutId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //通过实体类获取
//        T bean=mList.get(position);
//        convert(holder, bean);
        convert(holder, mList.get(position));
    }

    public abstract void convert(ViewHolder holder, T t);

    /**
     * 列表数据的数量
     */
    @Override
    public int getItemCount() {
        return this.mList == null ? 0 : this.mList.size();
    }

}