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
        parameters.add(new Parameter(name, para));
    }

    public boolean matches(Variable...vars){
        if(vars.length != parameters.size()) return false;
        for(int i = 0;i < vars.length;i++) if(parameters.get(i).var.canSet(vars[i].value())) return false;
        return true;
    }

    public Memory createMem(Variable...vars){
        Memory mem = new Memory();
        Variable var;

        for(int i = 0;i < vars.length; i++){
            var = parameters.get(i).var;
            var.set(vars[i].value(), vars[i].type());
            mem.set(parameters.get(i).name, var);
        }

        return mem;
    }
}
