package com.finance.commonlib.base.recycle;


import android.view.View;

import com.finance.commonlib.base.recycle.listener.OnItemClickListener;
import com.finance.commonlib.base.recycle.listener.OnItemLongClickListener;
import com.finance.commonlib.base.recycle.listener.OnViewClickListener;
import com.finance.commonlib.base.recycle.listener.OnViewLongClickListener;

import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;


/**
 * @author yinhui
 * @date 2019-08-16
 */
public abstract class SimpleArrayAdapter<T> extends BaseArrayAdapter<T> {

    private Type mType;

    public SimpleArrayAdapter() {
        this(null);
    }

    public SimpleArrayAdapter(List<T> data) {
        super(data);
        mType = new Type();
        register(mType);
    }

    public BaseViewHolder<T> createView(@NonNull View view, @NonNull ItemType<T, BaseViewHolder<T>> type) {
        return new BaseViewHolder<>(view, type);
    }

    public abstract int getLayoutId();

    public abstract void setupView(@NonNull BaseViewHolder<T> holder, T model, int position);

    public void setOnItemClickListener(OnItemClickListener<T> l) {
        mType.setOnItemClickListener(l);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> l) {
        mType.setOnItemLongClickListener(l);
    }

    public void addOnViewClickListener(@IdRes int id, OnViewClickListener<T> l) {
        mType.addOnViewClickListener(id, l);
    }

    public void addOnViewLongClickListener(@IdRes int id, OnViewLongClickListener<T> l) {
        mType.addOnViewLongClickListener(id, l);
    }

    private class Type extends ItemType<T, BaseViewHolder<T>> {

        @Override
        public int getLayoutId(int type) {
            return SimpleArrayAdapter.this.getLayoutId();
        }

        @NonNull
        @Override
        public BaseViewHolder<T> onCreateViewHolder(@NonNull View view, @NonNull ItemType<T, BaseViewHolder<T>> type) {
            return createView(view, type);
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<T> holder, T model, int position) {
            setupView(holder, model, position);
        }
    }
}
