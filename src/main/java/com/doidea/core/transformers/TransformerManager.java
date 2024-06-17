package com.doidea.core.transformers;

import com.doidea.core.Dispatcher;
import com.doidea.core.Launcher;

/**
 * 修改项管理
 */
public class TransformerManager {

    private final Dispatcher dispatcher;

    public TransformerManager(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * 准备 dispatcher 数据
     */
    public void preDispatcher() {

        // TODO 先执行隐藏操作
        // 【hideme】异常堆栈移除自定义类名（目前没生效，java.lang.* 核心类的原因？NEO大佬的也没生效，后续再看）
        //dispatcher.addTransformer(new ThrowableTransformer()); // 效果不对
        dispatcher.addTransformer(new ClassNameTransformer());
        // 【hideme】jvm 启动参数移除 `-javaagent:` 插件信息（有效）
        dispatcher.addTransformer(new VMTransformer());

        switch (Launcher.propMap.get("mode")) {
            case "0":
                // 去掉【试用已到期】提示弹窗
                dispatcher.addTransformer(new MessagesShowDialogTransformer());
                // 去掉 Licenses（许可证）弹窗
                dispatcher.addTransformer(new JDialogSetTitleTransformer());
                break;
            case "1":
                // 【url】拦截 validateKey.action 请求
                dispatcher.addTransformer(new HttpClientTransformer());
                // 【dns】域名解析屏蔽
                dispatcher.addTransformer(new InetAddressTransformer());
                // 【power】验签关键方法修改，BigInteger.oddModPow
                dispatcher.addTransformer(new BigIntegerTransformer());
                break;
            default:
        }

        // 以下为其他方式修改探索，不用管
        // obtainAnonTrial.action/validateKey.action 请求参数 userName 修改
        //dispatcher.addTransformer(new ParamUserNameTransformer());
        // obtainAnonTrial.action/validateKey.action 请求参数 machineId 修改
        //dispatcher.addTransformer(new ParamMachineIdTransformer());
        // validateKey.action 验证许可证密钥请求方法修改
        //dispatcher.addTransformer(new ValidateKeyRequestTransformer());
    }
}
