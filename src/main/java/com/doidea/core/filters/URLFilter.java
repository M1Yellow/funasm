package com.doidea.core.filters;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class URLFilter {

    private static final List<String> URLList = Arrays.asList(
            "validateKey.action" // 验证激活码
    );

    public static URL testURL(URL url) throws IOException {
        if (null == url || url.toString().trim().isEmpty() || null == URLList || URLList.isEmpty())
            return null;

        System.out.println(">>>> URLFilter testURL url: " + url);
        for (String key : URLList) {
            if (url.toString().contains(key)) throw new SocketTimeoutException();
        }
        return url;
    }
}
