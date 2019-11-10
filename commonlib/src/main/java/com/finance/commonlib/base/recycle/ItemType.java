package com.finance.commonlib.base.recycle;


import android.util.SparseArray;
import android.view.View;

import com.finance.commonlib.base.recycle.listener.OnItemClickListener;
import com.finance.commonlib.base.recycle.listener.OnItemLongClickListener;
import com.finance.commonlib.base.recycle.listener.OnViewClickListener;
import com.finance.commonlib.base.recycle.listener.OnViewLongClickListener;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;


/**
 * @author yinhui
 * @since 19-4-13
 */
public abstract class ItemType<T, VH extends BaseViewHolder<T>> {

    OnItemClickListener<T> mItemClickListener;
    OnItemLongClickListener<T> mItemLongClickListener;
    SparseArray<OnViewClickListener<T>> mViewClickListeners = new SparseArray<>();
    SparseArray<OnViewLongClickListener<T>> mViewLongClickListeners = new SparseArray<>();
    private BaseRecyclerAdapter<T> mAdapter;

    public void setOnItemClickListener(OnItemClickListener<T> l) {
        this.mItemClickListener = l;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> l) {
        this.mItemLongClickListener = l;
    }

    public void addOnViewClickListener(@IdRes int id, OnViewClickListener<T> l) {
        if (l != null) {
            this.mViewClickListeners.put(id, l);
        }
    }

    public void addOnViewLongClickListener(@IdRes int id, OnViewLongClickListener<T> l) {
        if (l != null) {
            this.mViewLongClickListeners.put(id, l);
        }
    }

    public abstract int getLayoutId(int type);

    public int getSpanSize() {
        return 1;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public VH onCreateViewHolder(@NonNull View view,
                                 @NonNull ItemType<T, VH> type) {
        return (VH) new BaseViewHolder<>(view, type);
    }

    public abstract void onBindViewHolder(@NonNull VH holder, T model, int position);

    void setAdapter(BaseRecyclerAdapter<T> adapter) {
        this.mAdapter = adapter;
    }

    public BaseRecyclerAdapter<T> getAdapter() {
        return mAdapter;
    }
}
