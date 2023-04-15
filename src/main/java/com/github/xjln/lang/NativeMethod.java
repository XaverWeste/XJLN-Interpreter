package com.github.xjln.lang;

import com.github.xjln.system.Memory;

public class NativeMethod extends Method{

    public interface Code{
        void execute(Object o, Memory mem);
    }

    public final Code code;
    public final ParameterList pl;

    public NativeMethod(ParameterList pl, Code code) {
        this.pl = pl;
        this.code = code;
    }
}
