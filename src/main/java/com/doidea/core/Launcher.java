package com.doidea.core;

import com.doidea.core.bo.TargetMethod;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
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
    public static final HashMap<String, List<TargetMethod>> targetClassMethodMap = new HashMap<>();

    static {
        // 无感知自动去掉 License 许可证到期时的弹窗，不退出程序，继续试用
        targetClassMethodMap.put("com." + "intel" + "lij" + ".openapi.ui.DialogWrapper",
                new ArrayList<TargetMethod>(1) {{ // 匿名内部类初始化
                    add(new TargetMethod("com." + "intel" + "lij" + ".openapi.ui" + ".DialogWrapper",
                            "setTitle", "(Ljava/lang/String;)V"));
                }});
        /*
        // TODO 获取试用许可只会请求一次，验证许可密钥每次启动都会请求
        // obtainAnonTrial.action/validateKey.action 请求参数：hostName、userName，在 base64 encode 之前，还处理了一次
        // invokestatic com/jet brains/t/t/ji j ([BJ)[B
        targetClassMethodMap.put("com.jet" + "brains.t.t.ji",
                new ArrayList<TargetMethod>(1) {{
                    add(new TargetMethod("com.jet" + "brains.t.t.ji",
                            "j", "([BJ)[B"));
                }});
        // machineId 生成方法 .method private static j(II)Ljava/lang/String;，搜 java/util/prefs/Preferences 定位
        targetClassMethodMap.put("com.jet" + "brains.t.t.p",
                new ArrayList<TargetMethod>(1) {{
                    add(new TargetMethod("com.jet" + "brains.t.t.p",
                            "j", "(II)Ljava/lang/String;"));
                }});
        // 干掉 validateKey.action 验证许可证密钥请求
        targetClassMethodMap.put("com.jet" + "brains.ls.requests.ValidateKeyRequest",
                new ArrayList<TargetMethod>(1) {{
                    add(new TargetMethod("com.jet" + "brains.ls.requests.ValidateKeyRequest",
                            "<init>", "(JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IJ)V"));
                }});
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
