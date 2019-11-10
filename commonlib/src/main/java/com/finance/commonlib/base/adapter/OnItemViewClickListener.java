package com.finance.commonlib.base.adapter;

import android.view.View;
import android.widget.Adapter;

/**
 * 点击项中的某个控件事件
 * @author Peng.zhong 2016-7-21 9:15:41
 *
 */
public interface OnItemViewClickListener {
	
	/**
	 * 
	 * @param adapter
	 * @param parent
	 * 被点击的视图
	 * @param v
	 * 视图中的控件
	 * @param position
	 * 视图在adapter中的位置
	 * @param id
	 * 视图的行id
	 */
	void onItemViewClick(Adapter adapter, View parent, View v, int position, long id);

}
