package com.finance.commonlib.base.recycle.listener;


import com.finance.commonlib.base.recycle.BaseViewHolder;

/**
 * @author yinhui
 * @since 19-4-18
 */
public interface OnItemClickListener<T> {

    /**
     * Called when a view has been clicked.
     *
     * @param holder   clicked view holder.
     * @param model    the item model attach to clicked view.
     * @param position position in list of clicked view.
     */
    void onClick(BaseViewHolder<T> holder, T model, int position);
}
