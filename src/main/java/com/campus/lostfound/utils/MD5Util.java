package com.campus.lostfound.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 */
public class MD5Util {
    
    /**
     * 对字符串进行MD5加密
     *
     * @param input 原始字符串
     * @return MD5加密后的字符串（32位小写）
     */
    public static String md5(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            
            // 将字节数组转换为16进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }
    
    /**
     * 验证字符串与MD5加密后的字符串是否匹配
     *
     * @param input 原始字符串
     * @param md5String MD5加密后的字符串
     * @return 是否匹配
     */
    public static boolean verify(String input, String md5String) {
        if (input == null || md5String == null) {
            return false;
        }
        return md5(input).equalsIgnoreCase(md5String);
    }
    
    /**
     * 生成带盐值的MD5加密
     *
     * @param input 原始字符串
     * @param salt 盐值
     * @return 带盐值的MD5加密字符串
     */
    public static String md5WithSalt(String input, String salt) {
        return md5(input + salt);
    }
}