package com.finance.commonlib.base.recycle;

import android.content.Context;

import android.util.ArrayMap;
import android.util.SparseArray;
import android.view.View;

import com.finance.commonlib.base.recycle.listener.OnItemClickListener;
import com.finance.commonlib.base.recycle.listener.OnItemLongClickListener;
import com.finance.commonlib.base.recycle.listener.OnViewClickListener;
import com.finance.commonlib.base.recycle.listener.OnViewLongClickListener;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Base view holder for {@link RecyclerView RecyclerView}.
 * This class provide following function:
 * 1. Cache of item view & sub views.
 * 2. Set a series of click listeners of item view or sub views.
 * 3. Set up views by item model.
 * 4. Interface of item type.
 *
 * @author yinhui
 * @since 17-12-23
 */
public class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews = new SparseArray<>();
    private T mItem;
    private int mPosition;
    private ArrayMap<String, Object> mTags = new ArrayMap<>();

    public BaseViewHolder(View itemView, ItemType<T, ?> type) {
        super(itemView);
        setOnItemClickListener(type.mItemClickListener);
        setOnItemLongClickListener(type.mItemLongClickListener);
        SparseArray<OnViewClickListener<T>> clickListeners = type.mViewClickListeners;
        for (int i = clickListeners.size() - 1; i >= 0; i--) {
            addOnClickListener(clickListeners.keyAt(i), clickListeners.valueAt(i));
        }
        SparseArray<OnViewLongClickListener<T>> longClickListeners = type.mViewLongClickListeners;
        for (int i = longClickListeners.size() - 1; i >= 0; i--) {
            addOnLongClickListener(longClickListeners.keyAt(i), longClickListeners.valueAt(i));
        }
    }

    public Context getContext() {
        return itemView.getContext();
    }

    void setup(T item, int pos) {
        this.mItem = item;
        this.mPosition = pos;
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V getView(int resId) {
        View view = mViews.get(resId);
        if (view == null) {
            view = itemView.findViewById(resId);
            if (view == null) {
                return null;
            }
            mViews.put(resId, view);
        }
        return (V) view;
    }

    public void putTag(String key, Object tag) {
        mTags.put(key, tag);
    }

    @SuppressWarnings("unchecked")
    public <Tag> Tag getTag(String key) {
        return (Tag) mTags.get(key);
    }

    public void setOnItemClickListener(final OnItemClickListener<T> l) {
        if (l != null) {
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    l.onClick(BaseViewHolder.this, mItem, mPosition);
                }
            });
        }
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener<T> l) {
        if (l != null) {
            this.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return l.onLongClick(BaseViewHolder.this, mItem, mPosition);
                }
            });
        }
    }

    public void addOnClickListener(@IdRes int id, final OnViewClickListener<T> l) {
        if (l != null) {
            getView(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    l.onClick(BaseViewHolder.this, mItem, mPosition);
                }
            });
        }
    }

    public void addOnLongClickListener(@IdRes int id, final OnViewLongClickListener<T> l) {
        if (l != null) {
            getView(id).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return l.onLongClick(BaseViewHolder.this, mItem, mPosition);
                }
            });
        }
    }

}
