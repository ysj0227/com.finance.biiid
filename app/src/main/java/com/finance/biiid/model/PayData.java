package com.finance.biiid.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author yangShiJie
 * @date 2019-11-12
 */
public class PayData {

    /**
     * appid : wxb4ba3c02aa476ea1
     * partnerid : 1900006771
     * package : Sign=WXPay
     * noncestr : 838decf3a4bd273ef7bb4026ee5d0646
     * timestamp : 1573107351
     * prepayid : wx0714155172462235a90cda451996623232
     * sign : A4D7DC206F0AC2788FDFD22F37F9397F
     */

    @SerializedName("appid")
    private String appid;
    @SerializedName("partnerid")
    private String partnerid;
    @SerializedName("package")
    private String packageX;
    @SerializedName("noncestr")
    private String noncestr;
    @SerializedName("timestamp")
    private int timestamp;
    @SerializedName("prepayid")
    private String prepayid;
    @SerializedName("sign")
    private String sign;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPackageX() {
        return packageX;
    }

    public void setPackageX(String packageX) {
        this.packageX = packageX;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
