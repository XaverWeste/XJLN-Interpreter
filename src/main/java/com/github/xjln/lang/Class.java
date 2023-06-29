package com.github.xjln.lang;

import com.github.xjln.system.Memory;
import com.github.xjln.system.System;

public class Class {
    public final Memory.ClassMemory mem;
    public final ParameterList pl;
    public final String constructor;
    public final String[] superClasses;

    public Class(ParameterList pl, String constructor, String[] superClasses){
        this.pl = pl;
        this.constructor = constructor;
        this.superClasses = superClasses;
        mem = new Memory.ClassMemory();
    }

    public Object createObject(){
        return new Object(mem.copy(), this);
    }

    public Method getMethod(String name, String[] parameters){
        Method m = mem.getM(name);
        if(m != null && m.getPl(parameters) != null) return m;
        for(String clas:superClasses){
            m = System.MEM.getC(clas).getMethod(name, parameters);
            if(m != null) return m;
        }
        return null;
    }
}
