package com.doidea.core.filters;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

/**
 * validateKey.action 请求拦截后，异常堆栈信息还是能看到插件类名，hideme 的效果不行？
 * java.net.SocketTimeoutException: connect timed out
 * at com.janetfilter.plugins.url.URLFilter.testURL(URLFilter.java:29)
 * at java.base/sun.net.www.http.HttpClient.openServer(HttpClient.java)
 */
public class URLFilter {

    private static List<String> URLList;

    public static void setURLList(List<String> URLList) {
        URLFilter.URLList = URLList;
    }

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
