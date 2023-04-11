package com.github.xjln.system;

import com.github.xjln.lang.Class;
import com.github.xjln.lang.Method;
import com.github.xjln.lang.Object;
import com.github.xjln.lang.Variable;

import java.util.HashMap;

public sealed class Memory permits Memory.SystemMemory, Memory.ClassMemory {

    public static final class SystemMemory extends Memory{
        private final HashMap<String,Class> classes=new HashMap<>();
        private final HashMap<String, Object> objects=new HashMap<>();

        public SystemMemory(){
            vars.put("result",new Variable());
        }

        public Class getC(String name){
            return classes.get(name);
        }

        public Object getO(String name){
            return objects.get(name);
        }

        public void set(String name,Class c){
            classes.put(name,c);
        }

        public void set(String name,Object o){
            objects.put(name,o);
        }
    }

    public static final class ClassMemory extends Memory{
        private final HashMap<String, Method> methods=new HashMap<>();

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

        public Memory copy(){
            Memory m=new Memory();
            Variable v;
            for(String s:vars.keySet()){
                v=vars.get(s);
                m.set(s,new Variable(v.value(),v.type(),v.constant()));
            }
            return m;
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
