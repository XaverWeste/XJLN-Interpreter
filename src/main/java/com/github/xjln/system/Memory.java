package com.github.xjln.system;


import com.github.xjln.lang.Method;
import com.github.xjln.lang.Variable;

import java.util.HashMap;

public sealed class Memory permits Memory.SystemMemory{

    public static final class SystemMemory extends Memory{

        private final HashMap<String, Method> methods=new HashMap<>();

        public SystemMemory(){
            vars.put("result",new Variable());
        }

        public Method getM(String name){
            return methods.get(name);
        }

        public void set(String name,Method m){
            if(methods.containsKey(name)) throw new RuntimeException("method " + name + "is already defined");
            else methods.put(name,m);
        }

        public boolean existM(String name){
            return methods.containsKey(name);
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

    public boolean exist(String name){
        return vars.containsKey(name);
    }
}
