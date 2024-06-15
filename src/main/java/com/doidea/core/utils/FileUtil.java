package com.doidea.core.utils;

import com.doidea.core.Launcher;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FileUtil {

    /**
     * 读取解析配置文件参数
     */
    public static Map<String, String> readPropConfig(String filePath) {
        if (null == filePath || filePath.trim().isEmpty()) return null;

        Map<String, String> propMap = new HashMap<>();

        // 按行读取文件内容到 Stream 流
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            // 解析内容数据，注意这里使随机行顺序
            lines.forEach(line -> {
                if (null == line || line.trim().isEmpty()) return; // return 实现 continue 效果
                // # 开头为注释
                if (line.startsWith("#")) return;
                // 没有包含 =
                if (!line.contains("=")) return;
                // 替换=两边的空格
                line = line.replace(" = ", "=");
                String[] eles = line.split("=");
                if (null == eles || eles.length < 2) return;
                propMap.put(eles[0], eles[1]);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 按文件行顺序进行处理
        //lines.forEachOrdered(System.out::println);
        // 按文件行顺序并行进行处理
        //lines.parallel().forEachOrdered(System.out::println);

        return propMap;
    }

    /**
     * 获取 Javaagent jar 包目录
     * 方法来自 NEO 大佬的 ja-netxxx 项目
     */
    public static URI getJarURI() throws Exception {
        URL url = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
        if (null != url) {
            System.out.println(">>>> getJarURI path: " + url.toURI().getPath());
            return url.toURI();
        }
        String resourcePath = "/doidea.properties";
        url = Launcher.class.getResource(resourcePath);
        if (null == url) throw new Exception("Can not locate resource file.");
        String path = url.getPath();
        System.out.println(">>>> getJarURI path: " + path);
        if (!path.endsWith("!" + resourcePath)) throw new Exception("Invalid resource path.");
        path = path.substring(0, path.length() - resourcePath.length() - 1);
        return new URI(path);
    }

}
