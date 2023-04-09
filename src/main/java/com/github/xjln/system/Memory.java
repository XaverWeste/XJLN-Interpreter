package com.github.xjln.system;


import com.github.xjln.lang.Variable;

import java.util.HashMap;

public sealed class Memory permits Memory.SystemMemory{

    public static final class SystemMemory extends Memory{

        public SystemMemory(){
            vars.put("result",new Variable());
        }
    }

    protected final HashMap<String,Variable> vars=new HashMap<>();

    public Variable get(String name){
        return vars.get(name);
    }

    public void set(String name,Variable var){
        if(vars.containsKey(name)) vars.get(name).set(var);
        else vars.put(name,var);
    }

    public int getVarSize(){
        return vars.keySet().size();
    }

    public boolean exist(String name){
        return vars.containsKey(name);
    }
}
