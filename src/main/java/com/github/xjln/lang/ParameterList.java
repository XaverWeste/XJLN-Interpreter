package com.github.xjln.lang;

import com.github.xjln.system.Memory;

import java.util.ArrayList;

public class ParameterList {

    private record Parameter(String name, Variable var){}

    private final ArrayList<Parameter> parameters;

    public ParameterList(){
        parameters = new ArrayList<>();
    }

    public void addParameter(String name, Variable para){
        for(Parameter p:parameters) if(p.name.equals(name)) throw new RuntimeException("variable " + name + " already exist in method definition");
        parameters.add(new Parameter(name, para));
    }

    public boolean matches(String...vars){
        if(vars.length != parameters.size()) return false;
        for(int i = 0;i < vars.length;i++) if(!parameters.get(i).var.canSet(vars[i])) return false;
        return true;
    }

    public Memory createMem(String...vars){
        Memory mem = new Memory();
        Variable var;

        for(int i = 0;i < vars.length; i++){
            var = parameters.get(i).var;
            var.set(vars[i], Variable.getType(vars[i]));
            mem.set(parameters.get(i).name, var);
        }

        return mem;
    }

    public boolean equals(ParameterList pl){
        if(pl.parameters.size() != parameters.size()) return false;
        for(int i = 0;i < parameters.size();i++) if(!parameters.get(i).var.type().equals(pl.parameters.get(i).var.type())) return false;
        return true;
    }
}
