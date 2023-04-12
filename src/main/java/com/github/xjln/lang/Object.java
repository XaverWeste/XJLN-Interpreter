package com.github.xjln.lang;

import com.github.xjln.system.Memory;

public class Object {
    public final Memory mem;
    public final Class clas;

    public Object(Memory m, Class clas){
        mem=m;
        this.clas = clas;
    }
}
