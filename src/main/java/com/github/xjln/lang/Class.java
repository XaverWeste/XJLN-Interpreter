package com.github.xjln.lang;

import com.github.xjln.system.Memory;

public class Class {
    public final Memory.ClassMemory mem=new Memory.ClassMemory();

    public Object createObject(){
        return new Object(mem.copy());
    }
}
