package com.qingfeng.utils;

import java.security.MessageDigest;

/**
 * @ProjectName MD5
 * @author Administrator
 * @version 1.0.0
 * @Description MD5
 * @createTime 2022/4/5 0005 23:49
 */
public class MD5 {

    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0){
                    i += 256;
                }
                if (i < 16){
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            str = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return str;
    }

    public static String md5Upper(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0){
                    i += 256;
                }
                if (i < 16){
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            str = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return str.toUpperCase();
    }

    public static void main(String[] args) {
        System.out.println(md5("11111@qq.com"+"123456"));
        System.out.println(md5("mj1"));

        String body = "{\"name\":\"qingfeng\"}";
        String str = "9897969594,bd5ba69e49baa74b1c68f5db23bf977f15f415b4,"+body;
        System.out.println(MD5.md5Upper(str));

    }
}
