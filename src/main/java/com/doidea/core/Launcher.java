package com.doidea.core;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.net.URL;
import java.util.jar.JarFile;

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
     * Javaagent 在程序启动前加载
     */
    public static void premain(String args, Instrumentation inst) {

        if (loaded) {
            System.err.println(">>>> multiple javaagent jar.");
            return;
        }

        try {
            URI jarURI = getJarURI();
            String path = jarURI.getPath();
            File agentFile = new File(path);
            System.out.println(">>>> jarURI.getPath(): " + path);
            // TODO jar 包类文件加入到 BootstrapClassLoader，以便可以在 ASM 代码中直接调用自定义类方法
            inst.appendToBootstrapClassLoaderSearch(new JarFile(agentFile));
            Initializer.init(inst);
            loaded = true;
        } catch (Throwable e) {
            System.err.println(">>>> Init instrumentation addTransformer error: " + e.getMessage());
            //e.printStackTrace();
        }
    }


    /**
     * 获取 Javaagent jar 包目录
     * 方法提取自 NEO 大佬的 ja-netxxx 项目
     */
    public static URI getJarURI() throws Exception {
        URL url = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
        if (null != url) return url.toURI();
        String resourcePath = "/6c81ec87e55d331c267262e892427a3d93d76683.txt";
        url = Launcher.class.getResource(resourcePath);
        if (null == url) throw new Exception("Can not locate resource file.");
        String path = url.getPath();
        if (!path.endsWith("!" + resourcePath)) throw new Exception("Invalid resource path.");
        path = path.substring(0, path.length() - resourcePath.length() - 1);
        return new URI(path);
    }
}
