package com.github.xjln.lang;

import com.github.xjln.system.Memory;

public class Class {
    public final Memory.ClassMemory mem;
    public final ParameterList pl;
    public final Class[] superClasses;

    public Class(ParameterList pl, Class[] superClasses){
        this.pl = pl;
        this.superClasses = superClasses;
        mem = new Memory.ClassMemory();
    }

    public Object createObject(){
        return new Object(mem.copy(), this);
    }

    public Method getMethod(String name, String[] parameters){
        if(mem.getM(name) != null && mem.getM(name).getPl(parameters) != null) return mem.getM(name);
        for(Class clas:superClasses) if(clas.mem.getM(name) != null && clas.mem.getM(name).getPl(parameters) != null) return clas.mem.getM(name);
        return null;
    }
}
