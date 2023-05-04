package com.github.xjln.lang;

public class Enum {

    public final String[] values;

    public Enum(String[] values){
        this.values = values;
    }

    public boolean isValue(String s){
        for(String value:values) if(value.equals(s)) return true;
        return false;
    }
}
