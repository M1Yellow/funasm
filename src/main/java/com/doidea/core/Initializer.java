package com.doidea.core;

import com.doidea.core.transformers.TransformerManager;

import java.lang.instrument.Instrumentation;

public class Initializer {
    public static void init(Instrumentation inst) {
        Dispatcher dispatcher = new Dispatcher();
        new TransformerManager(dispatcher).preDispatcher();
        inst.addTransformer(dispatcher);
    }
}
