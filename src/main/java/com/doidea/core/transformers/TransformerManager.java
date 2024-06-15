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

        switch (Launcher.propMap.get("mode")) {
            case "0":
                // 去掉【试用已到期】提示弹窗
                dispatcher.addTransformer(new MessagesShowDialogTransformer());
                // 去掉 Licenses（许可证）弹窗
                dispatcher.addTransformer(new JDialogSetTitleTransformer());
                break;
            case "1":
                // validateKey.action 请求拦截
                dispatcher.addTransformer(new HttpClientTransformer());
                // DNS 域名解析屏蔽
                dispatcher.addTransformer(new InetAddressTransformer());
                // 验签关键方法修改，BigInteger.oddModPow
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
