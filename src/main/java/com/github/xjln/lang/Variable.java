package com.github.xjln.lang;

import java.util.Objects;

public class Variable{

    private final boolean constant;
    private final String type;
    private String value;

    public Variable(String type,String value,boolean constant){
        this.constant=constant;
        this.type=type;
        this.value=value;
        check();
    }

    public Variable(String type){
        this.constant=false;
        this.type=type;
        this.value="";
        check();
    }

    public Variable(){
        this.constant=false;
        this.type="";
        this.value="";
        check();
    }

    public void set(Variable var){
        if(!constant&&(type==null|| type.equals(var.type))){
            value=var.value;
        }
    }

    public void set(String val,String ty){
        if(!constant&&(type.equals("")|| type.equals(ty))){
            value=val;
        }
    }

    public boolean canSet(String val){
        if(constant) return false;
        if(type.equals("")) return true;
        return getType(val).equals(type);
    }

    public String value(){
        return value;
    }

    public String type(){
        return type;
    }

    public boolean constant(){
        return constant;
    }

    private void check(){
        if(!type.equals("") && !value.equals("") && !getType(value).equals(type)) throw new RuntimeException("illegal argument");
    }

    public static String getType(String value){
        if(value.startsWith("\"")&&value.endsWith("\"")) return "str";
        if(value.matches("^[0-9.]+$")) return "num";
        if(value.equals("true")||value.equals("false")) return "bool";
        if(value.equals("")) return "";
        throw new RuntimeException("illegal argument");
    }
}
