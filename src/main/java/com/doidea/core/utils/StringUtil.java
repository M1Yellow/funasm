package com.doidea.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

}
