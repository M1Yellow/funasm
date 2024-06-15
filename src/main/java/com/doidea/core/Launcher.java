package com.doidea.core;

import com.doidea.core.utils.FileUtil;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;


/**
 * TODO 先在命令行执行一次，测试是否运行正常 👇
 * <br>
 * java -javaagent:doidea-asm-2.0.0.jar Test
 * <br>
 * 有打印日志和配置参数，说明插件可以运行，Test 不存在不用管
 * <br>
 * >>>> agentArgs: null<br>
 * >>>> getJarURI path: /E:/DevRes/doidea/doidea-asm-2.0.0.jar<br>
 * >>>> jarURI.getPath(): /E:/DevRes/doidea/doidea-asm-2.0.0.jar<br>
 * >>>> initConfig configFilePath: E:\DevRes\doidea\doidea.properties<br>
 */
public class Launcher {

    /**
     * javaagent 是否已加载完成
     */
    private static volatile boolean loaded = false;

    /**
     * 插件全局配置参数
     */
    public static Map<String, String> propMap;


    public static void main(String[] args) {
        // 程序自身的日志不会在IDEA日志中打印
        System.out.println(">>>> Launcher main >>>>");
    }

    /**
     * 在 JVM 启动时加载，在程序 main 方法执行之前被调用
     */
    public static void premain(String args, Instrumentation inst) {

        if (loaded) {
            System.err.println(">>>> multiple javaagent jar.");
            return;
        }

        // TODO 获取参数设置
        System.out.println(">>>> agentArgs: " + args);
        // java -javaagent:agent1.jar=key1=value1&key2=value2 -javaagent:agent2.jar -jar Test.jar
        // args 值为 key1=value1&key2=value2

        try {
            URI jarURI = FileUtil.getJarURI();
            String path = jarURI.getPath();
            File agentFile = new File(path);
            System.out.println(">>>> jarURI.getPath(): " + path); // /E:/DevRes/doidea/doidea-asm-2.0.0.jar
            // TODO jar 包类文件加入到 BootstrapClassLoader，以便可以在 ASM 代码中直接调用自定义类方法
            inst.appendToBootstrapClassLoaderSearch(new JarFile(agentFile));
            Map<String, Object> params = new HashMap<>(); // 可以指定初始容量
            params.put("configFilePath", new File(agentFile.getParentFile().getPath(), "doidea.properties").getPath());
            // 执行初始化
            Initializer.init(inst, params);
            // 标记已加载
            loaded = true;
        } catch (Throwable e) {
            System.err.println(">>>> Init instrumentation addTransformer error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
