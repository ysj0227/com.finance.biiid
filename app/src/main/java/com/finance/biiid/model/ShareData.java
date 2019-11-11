package com.finance.biiid.model;

/**
 * 微信分享数据
 */
public class ShareData {
//        {
//            "title":"测试分享描述",
//                "desc":"测试分享描述",
//                "link":"https://pai.qianyusoft.cn/ddq_front/entrance.html",
//                "imgUrl":"https://wei.bidddq.com/imgs/logo.jpg"
//        }
    private String title;
    private String desc;
    private String link;
    private String imgUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
