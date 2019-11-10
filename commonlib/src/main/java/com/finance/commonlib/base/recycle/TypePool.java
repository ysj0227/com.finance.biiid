package com.finance.commonlib.base.recycle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yinhui
 * @since 19-4-13
 */
public class TypePool implements ITypePool {

    private final List<Class<?>> classes;
    private final List<ItemType> types;
    private boolean isMultiType = true;

    TypePool() {
        this.classes = new ArrayList<>();
        this.types = new ArrayList<>();
    }

    @Override
    public void register(Class<?> itemClass, ItemType itemType) {
        if (itemClass == null && !types.isEmpty()) {
            throw new IllegalStateException();
        } else if (itemClass != null && !isMultiType) {
            throw new IllegalStateException();
        }
        if (classes.contains(itemClass)) {
            return;
        }
        if (itemClass == null) {
            isMultiType = false;
        } else {
            classes.add(itemClass);
        }
        types.add(itemType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, VH extends BaseViewHolder<T>> ItemType<T, VH> getType(int viewType) {
        return types.get(viewType);
    }

    @SuppressWarnings("unchecked")
    public <T, VH extends BaseViewHolder<T>> ItemType<T, VH> getType(Class<T> itemClass) {
        if (isMultiType) {
            return types.get(classes.indexOf(itemClass));
        } else {
            return types.get(0);
        }
    }

    public void clear() {
        isMultiType = true;
        classes.clear();
        types.clear();
    }

    @Override
    public int getIndexOfType(Class<?> itemClass) {
        if (isMultiType) {
            return classes.indexOf(itemClass);
        } else {
            return 0;
        }
    }
}
