package com.finance.commonlib.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {


    public static boolean isChinaPhone(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(16[0-9])|(17[0-9])|(18[0-9])|(19[8-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static String handleIllegalCharacter(String s) {
        if (TextUtils.isEmpty(s)) {
            return s;
        }
        // 前后空格
        s = s.trim();

        //去除：空格\s,回车\n,水平制表符即tab \t,换行\r
        Pattern p = Pattern.compile("\\s|\n|\t|\r");
        Matcher m = p.matcher(s);
        s = m.replaceAll("");

        // Excel文档中非法字符
        if (s.contains("\u202C")) {
            s = s.replace("\u202C", "").trim();
        }
        if (s.contains("\u202D")) {
            s = s.replace("\u202D", "").trim();
        }
        if (s.contains("\u202E")) {
            s = s.replace("\u202E", "").trim();
        }
        return s;
    }

    public static boolean isFixedPhone(String phone) {
        Pattern p1, p2;
        Matcher m;
        boolean isPhone;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$"); // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");     // 验证没有区号的
        if (phone.length() > 9) {
            m = p1.matcher(phone);
            isPhone = m.matches();
        } else {
            m = p2.matcher(phone);
            isPhone = m.matches();
        }
        return isPhone;
    }

    /**
     * 数字字母组合，不能全是数组，不能全是字母，8-30位
     */
    public static boolean isValidPassword(String str) {
        //(?!^\\d+$)(?!^[a-zA-Z]+$)(?!^[_#@]+$).{8,30}
        //Pattern pattern = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,30}$");//不包含符号
        Pattern pattern = Pattern.compile("^(?![A-Za-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{8,30}$");//包含符号
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    /**
     * 判断是否为邮箱
     *
     * @param emails
     * @return
     */
    public static boolean isEmail(String emails) {
        String reg = "^\\w+((-\\w+)|(.\\w+))*@[A-Za-z0-9]+((.|-)[A-Za-z0-9]+)*.[A-Za-z0-9]+$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(emails);
        return m.matches();
    }

    //mac地址合法性校验
    public static boolean isValidMac(String macStr) {
        if (TextUtils.isEmpty(macStr)) {
            return false;
        }
        String macAddressRule = "([A-Fa-f0-9]{2}[-,:]){5}[A-Fa-f0-9]{2}";
        return macStr.matches(macAddressRule);
    }

    /**
     * 校验ip是否合法
     */
    public static boolean isIP(final String input) {
        Pattern IPV4_PATTERN = Pattern.compile(
                "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
        return IPV4_PATTERN.matcher(input).matches();
    }

    /**
     * 非D类、E类，非全0、非全1、非回环
     */
    public static boolean isInvalidDns(final String input) {
        if (TextUtils.isEmpty(input)) return true;
        Pattern IPV4_PATTERN = Pattern.compile(
                "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
        boolean isIp = IPV4_PATTERN.matcher(input).matches();
        if (!isIp) return true;
        int first = Integer.parseInt(input.split("\\.")[0]);
        return first == 127 || first >= 224 || TextUtils.equals("0.0.0.0", input)
                || TextUtils.equals("255.255.255.255", input);
    }

    /**
     * 校验子网掩码是否合法
     */
    public static boolean isValidSubnetMask(final String input) {
        return !TextUtils.equals(input, "0.0.0.0")
                && !TextUtils.equals(input, "255.255.255.255");
    }

    // IpV4的正则表达式，用于判断IpV4地址是否合法
    private static final String IPV4_REGEX = "((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])" +
            "(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})";

    /**
     * 判断ipV4或者mask地址是否合法，通过正则表达式方式进行判断
     *
     * @param ipv4
     */
    public static boolean ipV4Validate(String ipv4) {
        return ipv4Validate(ipv4, IPV4_REGEX);
    }

    /**
     * 比较两个ip地址是否在同一个网段中，如果两个都是合法地址，两个都是非法地址时，可以正常比较；
     * 如果有其一不是合法地址则返回false；
     * 注意此处的ip地址指的是如“192.168.1.1”地址
     */
    public static boolean checkSameSegment(String ip1, String ip2, int mask) {
        int ipValue1 = getIpV4Value(ip1);
        int ipValue2 = getIpV4Value(ip2);
        return (mask & ipValue1) == (mask & ipValue2);
    }

    private static boolean ipv4Validate(String addr, String regex) {
        if (addr == null) {
            return false;
        } else {
            return Pattern.matches(regex, addr.trim());
        }
    }

    /**
     * 比较两个ip地址，如果两个都是合法地址，则1代表ip1大于ip2，-1代表ip1小于ip2,0代表相等；
     * 如果有其一不是合法地址，如ip2不是合法地址，则ip1大于ip2，返回1，反之返回-1；两个都是非法地址时，则返回0；
     * 注意此处的ip地址指的是如“192.168.1.1”地址，并不包括mask
     *
     * @return
     */
    public static int compareIpV4s(String ip1, String ip2) {
        int result = 0;
        int ipValue1 = getIpV4Value(ip1);     // 获取ip1的32bit值
        int ipValue2 = getIpV4Value(ip2); // 获取ip2的32bit值
        if (ipValue1 > ipValue2) {
            result = -1;
        } else {
            result = 1;
        }
        return result;
    }

    public static int getIpV4Value(String ipOrMask) {
        byte[] addr = getIpV4Bytes(ipOrMask);
        int address1 = addr[3] & 0xFF;
        address1 |= ((addr[2] << 8) & 0xFF00);
        address1 |= ((addr[1] << 16) & 0xFF0000);
        address1 |= ((addr[0] << 24) & 0xFF000000);
        return address1;
    }

    public static byte[] getIpV4Bytes(String ipOrMask) {
        try {
            String[] addrs = ipOrMask.split("\\.");
            int length = addrs.length;
            byte[] addr = new byte[length];
            for (int index = 0; index < length; index++) {
                addr[index] = (byte) (Integer.parseInt(addrs[index]) & 0xff);
            }
            return addr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[4];
    }

}
