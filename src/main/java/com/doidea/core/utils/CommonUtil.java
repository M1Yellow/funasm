package com.doidea.core.utils;

public class CommonUtil {

    /**
     * 混淆异常堆栈信息
     */
    public static <T extends Throwable> T mixExceptionStackTrace(T e, String key) {
        if (null == e) return null;
        if (null == key || key.trim().isEmpty()) return e;
        StackTraceElement[] stackTrace = e.getStackTrace();

        boolean isMod = false;
        try {
            for (int i = 0; i < stackTrace.length; i++) {
                if (stackTrace[i].getClassName().replace("/", ".").contains(key)) {
                    // 自定义堆栈元素
                    StackTraceElement element = new StackTraceElement(
                            StringUtil.getRandomClassNameDefault(), StringUtil.getRandomClassNameDefault(),
                            StringUtil.getRandomLengthString(10), (int) (Math.random() * 1000) + 1);
                    stackTrace[i] = element;
                    isMod = true;
                }
            }
            if (isMod) e.setStackTrace(stackTrace);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return e;
    }

}
