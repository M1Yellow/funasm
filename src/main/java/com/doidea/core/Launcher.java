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
        // 无感知自动去掉 License 许可证到期时的弹窗，不退出程序，继续试用
        targetClassMethodMap.put("com." + "intel" + "lij" + ".openapi.ui.DialogWrapper", Arrays.asList("setTitle"));
        /*
        //targetClassMethodMap.put("java.net.URL", Arrays.asList("<init>")); // 没有加载这个类
        // TODO 获取试用许可只会请求一次，验证许可密钥每次启动都会请求
        // obtainAnonTrial.action/validateKey.action 请求参数：hostName、userName，在 base64 encode 之前，还处理了一次
        // invokestatic com/jetbrains/t/t/ji j ([BJ)[B
        targetClassMethodMap.put("com.jetbrains.t.t.ji", Arrays.asList("j"));
        // machineId 生成方法 .method private static j(II)Ljava/lang/String;，搜 java/util/prefs/Preferences 定位
        targetClassMethodMap.put("com.jetbrains.t.t.p", Arrays.asList("j"));
        // 干掉 validateKey.action 验证许可证密钥请求
        targetClassMethodMap.put("com.jetbrains.ls.requests.ValidateKeyRequest", Arrays.asList("<init>"));
        */
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