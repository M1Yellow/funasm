package com.doidea.core.transformers;

import com.doidea.core.Dispatcher;

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
        // 去掉【试用已到期】提示弹窗
        dispatcher.addTransformer(new MessagesShowDialogTransformer());
        // 去掉 Licenses（许可证）弹窗
        dispatcher.addTransformer(new JDialogSetTitleTransformer());
        // obtainAnonTrial.action/validateKey.action 请求参数 userName 修改
        //dispatcher.addTransformer(new ParamUserNameTransformer());
        // obtainAnonTrial.action/validateKey.action 请求参数 machineId 修改
        //dispatcher.addTransformer(new ParamMachineIdTransformer());
        // validateKey.action 验证许可证密钥请求方法修改
        //dispatcher.addTransformer(new ValidateKeyRequestTransformer());
    }
}
