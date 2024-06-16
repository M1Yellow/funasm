package com.doidea.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class StringUtil {

    public static List<String> splitStrToList(String s, String splitStr) {
        if (null == s || s.trim().isEmpty()) return null;
        List<String> list = new ArrayList<>();
        if (null == splitStr || splitStr.trim().isEmpty()) {
            list.add(s);
            return list;
        }
        String[] vals = s.split(splitStr);
        if (null == vals || vals.length < 1) return null;

        return Arrays.asList(vals);
    }

    public static String obj2Str(Object o) {
        if (null == o) return null;
        return o.toString();
    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 生成长度在 [1, range] 范围内的随机字符串
     */
    public static String getRandomLengthString(int range) {
        int len = (int) (Math.random() * range) + 1;
        return getRandomString(len);
    }

}
