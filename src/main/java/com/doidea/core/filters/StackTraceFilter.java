package com.doidea.core.filters;

import com.doidea.core.utils.StringUtil;

public class StackTraceFilter {

    public static StackTraceElement[] testStackTrace(StackTraceElement[] stackTrace) {
        // 太多日志
        //System.out.println(">>>> StackTraceFilter testStackTrace stackTrace is null: " + (null == stackTrace));
        if (null == stackTrace || stackTrace.length < 1) return stackTrace;

        //System.out.println(">>>> StackTraceFilter testStackTrace length: " + stackTrace.length);
        System.out.println(">>>> StackTraceFilter testStackTrace sample: " + stackTrace[0].toString());
        System.out.println(">>>> StackTraceFilter testStackTrace className: " + stackTrace[0].getClassName());
        try {
            for (int i = 0; i < stackTrace.length; i++) {
                if (stackTrace[i].getClassName().replace("/", ".").contains("doidea")) {
                    // 自定义堆栈元素
                    StackTraceElement element = new StackTraceElement(
                            StringUtil.getRandomClassNameDefault(), StringUtil.getRandomClassNameDefault(),
                            StringUtil.getRandomLengthString(10), (int) (Math.random() * 1000) + 1);
                    stackTrace[i] = element;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stackTrace;
    }

}
