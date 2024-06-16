package com.doidea.core;

import com.doidea.core.filters.BigIntegerFilter;
import com.doidea.core.filters.DNSFilter;
import com.doidea.core.filters.URLFilter;
import com.doidea.core.transformers.TransformerManager;
import com.doidea.core.utils.FileUtil;
import com.doidea.core.utils.StringUtil;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Initializer {

    /**
     * 初始化插件
     */
    public static void init(Instrumentation inst, Map<String, Object> params) throws Exception {
        // 初始化插件配置参数
        initConfig(params);
        // 创建 transform 调度器
        Dispatcher dispatcher = new Dispatcher();
        // 实例化 transform 管理
        new TransformerManager(dispatcher).preDispatcher();
        // Instrumentation 设置 transform
        inst.addTransformer(dispatcher);
    }


    /**
     * 初始化插件全局参数配置
     */
    public static void initConfig(Map<String, Object> params) throws Exception {
        // 读取全局配置文件
        String configFilePath = params.get("configFilePath").toString();
        System.out.println(">>>> initConfig configFilePath: " + configFilePath);
        Launcher.propMap = FileUtil.readPropConfig(configFilePath);
        if (null == Launcher.propMap || Launcher.propMap.isEmpty()) {
            System.err.println(">>>> 读取全局配置文件失败，请检查配置文件是否存在");
            //throw new RuntimeException("读取全局配置文件失败");
            // TODO 没找到配置文件，或者配置解析失败，默认使用去授权弹窗模式
            System.out.println(">>>> 没找到配置文件，或者配置解析失败，默认使用去授权弹窗模式");
            if (null == Launcher.propMap) Launcher.propMap = new HashMap<>();
            Launcher.propMap.put("mode", "0");
            return;
        }
        // 打印全局配置参数
        System.out.println(">>>> 插件全局配置（顺序随机）↓");
        for (Map.Entry<String, String> entry : Launcher.propMap.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
        // 设置 url filter 参数
        List<String> urlList = StringUtil.splitStrToList(Launcher.propMap.get("url"), ",");
        if (null != urlList && !urlList.isEmpty()) URLFilter.setURLList(urlList);
        // 设置 dns filter 参数
        List<String> dnsList = StringUtil.splitStrToList(Launcher.propMap.get("dns"), ",");
        if (null != dnsList && !dnsList.isEmpty()) DNSFilter.setDNSList(dnsList);
        // 设置 BigInteger filter 参数
        String x = Launcher.propMap.get("odd-mod-pow.x");
        String y = Launcher.propMap.get("odd-mod-pow.y");
        String z = Launcher.propMap.get("odd-mod-pow.z");
        String r = Launcher.propMap.get("odd-mod-pow.r");
        if (null != x && !x.trim().isEmpty()
                && null != y && !y.trim().isEmpty()
                && null != z && !z.trim().isEmpty()
                && null != r && !r.trim().isEmpty()) {
            BigIntegerFilter.setX(x);
            BigIntegerFilter.setY(y);
            BigIntegerFilter.setZ(z);
            BigIntegerFilter.setR(r);
        }
    }
}
