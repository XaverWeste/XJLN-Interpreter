package com.github.xjln.lang;

public class Variable{

    private final boolean constant;
    private final String type;
    private String value;

    public Variable(String value,String type,boolean constant){
        this.constant=constant;
        this.type=type;
        this.value=value;
    }

    public Variable(String type){
        this.constant=false;
        this.type=type;
        this.value="";
    }

    public Variable(){
        this.constant=false;
        this.type="";
        this.value="";
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

    public String value(){
        return value;
    }

    public String type(){
        return type;
    }

    public boolean constant(){
        return constant;
    }

    public static String getType(String value){
        if(value.startsWith("\"")&&value.endsWith("\"")) return "str";
        if(value.matches("^[0-9.]+$")) return "num";
        if(value.equals("true")||value.equals("false")) return "bool";
        if(value.startsWith("§")) return "class";
        if(value.startsWith("{")&&value.endsWith("}")) return "arr";
        return null;
    }
}
