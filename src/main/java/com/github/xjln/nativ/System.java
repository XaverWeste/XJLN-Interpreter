package com.github.xjln.nativ;

import com.github.xjln.lang.NativeClass;
import com.github.xjln.lang.Object;
import com.github.xjln.lang.ParameterList;
import com.github.xjln.lang.Variable;

public class System extends NativeClass {

    public System() {
        super(new ParameterList());
    }

    @Override
    public Variable execute(String name, String[] paras, Object o) {
        switch (name){
            case "log" -> log(paras);
        }
        return null;
    }

    private void log(String[] paras){
        if(paras.length != 1) throw new RuntimeException("to many parameters ");
        java.lang.System.out.println(paras[0]);
    }
}
