package com.github.xjln.lang;

import com.github.xjln.system.Memory;

public class NativeMethod extends Method{

    public interface Code{
        void execute(Object o, Memory mem);
    }

    public final Code code;

    public NativeMethod(ParameterList pl, Code code) {
        super(pl, "");
        this.code = code;
    }
}
