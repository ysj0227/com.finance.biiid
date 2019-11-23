package com.finance.biiid.config;

public class WxShareConfig {
    public static boolean isHideShare(String title) {
        return title.contains("发布拍品") || title.contains("点点有礼") ||
                title.contains("分销") || title.contains("每日一送") ||
                title.contains("联系信息") || title.contains("订单列表") ||
                title.contains("二维码") || title.contains("粉丝") ||
                title.contains("客服") || title.contains("反馈") ||
                title.contains("余额") || title.contains("提现") ||
                title.contains("消息详情") || title.contains("东东抢消息");
    }
}
