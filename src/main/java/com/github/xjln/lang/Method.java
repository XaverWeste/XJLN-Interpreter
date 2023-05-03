package com.github.xjln.lang;

import com.github.xjln.interpreter.AST;

import java.util.HashMap;

public class Method{

    private final HashMap<ParameterList, AST[]> map;

    public Method(){
        map = new HashMap<>();
    }

    public void add(ParameterList pl, AST[] ast){
        for(ParameterList p: map.keySet()) if(p.equals(pl)) throw new RuntimeException("Method already exists");
        map.put(pl, ast);
    }

    public ParameterList getPl(String[] paras){
        for (ParameterList p: map.keySet()) if(p.matches(paras)) return p;
        throw new RuntimeException("illegal argument");
    }

    public AST[] getCode(ParameterList pl){
        return map.get(pl);
    }
}
