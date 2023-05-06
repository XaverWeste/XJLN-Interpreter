package com.github.xjln.lang;

import com.github.xjln.system.Memory;

public class Enum {

    public final String[] values;
    public final Memory.ClassMemory mem;

    public Enum(String[] values){
        this.values = values;
        mem = new Memory.ClassMemory();
    }

    public boolean isValue(String s){
        for(String value:values) if(value.equals(s)) return true;
        return false;
    }
}
