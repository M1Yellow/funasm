package com.doidea.core;

import java.lang.instrument.Instrumentation;

public class Launcher {

    /**
     * javaagent 是否已加载完成
     */
    private static volatile boolean loaded = false;

    public static void main(String[] args) {
        // 程序自身的日志不会在IDEA日志中打印
        System.out.println(">>>> Launcher main >>>>");
    }

    /**
     * 在 JVM 启动前加载
     */
    public static void premain(String args, Instrumentation instrumentation) {

        if (loaded) {
            System.err.println(">>>> multiple javaagent jar.");
            return;
        }

        try {
            Initializer.init(instrumentation);
            loaded = true;
        } catch (Throwable e) {
            System.err.println(">>>> Init instrumentation addTransformer error: " + e.getMessage());
            //e.printStackTrace();
        }
    }
}
