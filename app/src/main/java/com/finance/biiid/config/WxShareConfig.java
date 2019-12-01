package com.finance.biiid.config;

import android.content.Context;

import com.finance.biiid.R;

public class WxShareConfig {
    public static boolean isHideShare(Context context,String title) {
        return title.contains(context.getString(R.string.title_publish)) || title.contains(context.getString(R.string.title_click)) ||
                title.contains(context.getString(R.string.title_sale)) || title.contains(context.getString(R.string.title_everyday_send)) ||
                title.contains(context.getString(R.string.title_contacts)) || title.contains(context.getString(R.string.title_order_list)) ||
                title.contains(context.getString(R.string.title_qr)) || title.contains(context.getString(R.string.title_fances)) ||
                title.contains(context.getString(R.string.title_server)) || title.contains(context.getString(R.string.title_suggest)) ||
                title.contains(context.getString(R.string.title_balance)) || title.contains(context.getString(R.string.title_withdraw)) ||
                title.contains(context.getString(R.string.title_mes_details)) || title.contains(context.getString(R.string.title_ddq_meg)) ||
                title.contains(context.getString(R.string.title_pay_sure)) || title.contains(context.getString(R.string.title_free_rush)) ||
                title.contains(context.getString(R.string.title_cast));
    }
}
