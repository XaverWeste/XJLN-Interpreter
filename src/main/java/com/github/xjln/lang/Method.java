package com.github.xjln.lang;

import java.util.HashMap;

public class Method{

    private final HashMap<ParameterList, String> map;

    public Method(){
        map = new HashMap<>();
    }

    public void add(ParameterList pl, String code){
        for(ParameterList p: map.keySet()) if(p.equals(pl)) throw new RuntimeException("Method already exists");
        map.put(pl, code);
    }

    public ParameterList getPl(String[] paras){
        for (ParameterList p: map.keySet()) if(p.matches(paras)) return p;
        throw new RuntimeException("illegal argument");
    }

    public String getCode(ParameterList pl){
        return map.get(pl);
    }
}
