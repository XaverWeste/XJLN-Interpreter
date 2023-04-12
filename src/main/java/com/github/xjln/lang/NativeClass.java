package com.github.xjln.lang;

public abstract class NativeClass extends Class{

    public NativeClass(ParameterList pl) {
        super(pl);
    }

    public abstract Variable execute(String name, String[] paras, Object o);
}
