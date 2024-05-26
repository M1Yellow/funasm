package com.doidea.core.bo;

import java.io.Serial;
import java.io.Serializable;

public class TargetMethod implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String targetClassName;
    private String targetMethodName;
    private Object targetMethodParamType;

    public TargetMethod(String targetClassName, String targetMethodName, Object targetMethodParamType) {
        this.targetClassName = targetClassName;
        this.targetMethodName = targetMethodName;
        this.targetMethodParamType = targetMethodParamType;
    }


    public String getTargetClassName() {
        return targetClassName;
    }

    public void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }

    public String getTargetMethodName() {
        return targetMethodName;
    }

    public void setTargetMethodName(String targetMethodName) {
        this.targetMethodName = targetMethodName;
    }

    public Object getTargetMethodParamType() {
        return targetMethodParamType;
    }

    public void setTargetMethodParamType(Object targetMethodParamType) {
        this.targetMethodParamType = targetMethodParamType;
    }
}
