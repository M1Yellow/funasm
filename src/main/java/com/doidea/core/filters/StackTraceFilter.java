package com.doidea.core.filters;

import com.doidea.core.utils.StringUtil;

public class StackTraceFilter {

    public static StackTraceElement[] testStackTrace(StackTraceElement[] stackTrace) {
        if (null == stackTrace || stackTrace.length < 1) return stackTrace;

        try {
            // 自定义堆栈元素
            StackTraceElement element = new StackTraceElement(stackTrace.getClass().getClassLoader().getName(),
                    StringUtil.getRandomLengthString(10), StringUtil.getRandomLengthString(5),
                    StringUtil.getRandomLengthString(10), StringUtil.getRandomLengthString(10),
                    StringUtil.getRandomLengthString(10), (int) (Math.random() * 1000) + 1);

            for (StackTraceElement e : stackTrace) {
                if (e.getClassName().startsWith("com.doidea.")) e = element;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stackTrace;
    }

}
