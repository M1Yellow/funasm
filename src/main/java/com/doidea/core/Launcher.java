package com.doidea.core;

import com.doidea.core.domain.MyClassFileTransformer;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Launcher {

    /**
     * javaagent 是否已加载完成
     */
    private static volatile boolean loaded = false;

    /**
     * 目标类和方法，适用于指定多个类、多个方法
     */
    public static final HashMap<String, List<String>> targetClassMethodMap = new HashMap<>();

    static {
        // 初始设置
        targetClassMethodMap.put("com." + "intel" + "lij" + ".openapi.ui.DialogWrapper", Arrays.asList("setTitle"));
        //targetClassMethodMap.put("java.net.URL", Arrays.asList("<init>")); // 没有加载这个类
        //targetClassMethodMap.put("com.jet" + "brains.t.t.jE", Arrays.asList("n"));
    }

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
            MyClassFileTransformer transformer = new MyClassFileTransformer();
            instrumentation.addTransformer(transformer);
            //instrumentation.addTransformer(transformer, true);
            loaded = true;
        } catch (Exception e) {
            System.err.println(">>>> instrumentation addTransformer failed.");
            e.printStackTrace();
        }
    }
}
