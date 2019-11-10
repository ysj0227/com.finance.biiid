package com.finance.commonlib.utils;

import android.text.TextUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Description:
 * Created by bruce on 2019/4/1.
 */
public class ByteUtils {
    /**
     * 合并字节数组
     *
     * @param values
     * @return
     */
    public static byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    //byte[]字节 转化string 的16进制
    public static String bytesToHex(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)));
        }
        return buf.toString();
    }

    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] hexToBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    /**
     * 整数转换为2字节的byte数组，低位到高位
     */
    public static byte[] intToByte2L(int i) {
        byte[] targets = new byte[2];
        targets[0] = (byte) (i >> 8 & 0xFF);
        targets[1] = (byte) (i & 0xFF);
        return targets;
    }

    public static String byte2String(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.toString();
    }

    public static byte[] String2Byte64(String message) {
        byte[] byte64 = new byte[64];
        Arrays.fill(byte64, (byte) 0);
        if (TextUtils.isEmpty(message)) {
            return byte64;
        }
        int len = message.length();
//        byte[] bytes = new byte[len];
        char[] chars = message.toCharArray();
        for (int i = 0; i < len; i++) {
            byte64[i] = (byte) chars[i];
        }
        return byte64;
    }

    public static byte[] getNoneByte64() {
        byte[] byte64 = new byte[64];
        Arrays.fill(byte64, (byte) 0);
        return byte64;
    }

    /**
     * int整数转换为4字节的byte数组
     *
     * @param i
     * @return byte
     */
    public static byte[] intToByte2(int i) {
        byte[] targets = new byte[2];
        targets[1] = (byte) (i & 0xFF);
        targets[0] = (byte) (i >> 8 & 0xFF);
        return targets;
    }

    public static int byte2ToInt(byte[] bytes) {
        int b0 = bytes[0] & 0xFF;
        int b1 = bytes[1] & 0xFF;
        return (b1 << 8) | b0;
    }

    /**
     * int整数转换为4字节的byte数组
     *
     * @param i
     * @return byte
     */
    public static byte[] intToByte4(int i) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (i & 0xFF);
        targets[2] = (byte) (i >> 8 & 0xFF);
        targets[1] = (byte) (i >> 16 & 0xFF);
        targets[0] = (byte) (i >> 24 & 0xFF);
        return targets;
    }

    public static int byte4ToInt(byte[] bytes) {
        int b0 = bytes[0] & 0xFF;
        int b1 = bytes[1] & 0xFF;
        int b2 = bytes[2] & 0xFF;
        int b3 = bytes[3] & 0xFF;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }

    public static int byte4ToIntL(byte[] bytes){
        int b0 = bytes[3] & 0xFF;
        int b1 = bytes[2] & 0xFF;
        int b2 = bytes[1] & 0xFF;
        int b3 = bytes[0] & 0xFF;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }
}
