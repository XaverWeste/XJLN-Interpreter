package com.github.xjln.lang;

import com.github.xjln.interpreter.Token;

public class Variable{

    //TODO rework

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
        if(!(constant && !value.equals("")) && (type.equals("") || type.equals(var.type))){
            value=var.value;
        } else {
            if(constant) throw new RuntimeException("variable is constant");
            throw new RuntimeException("expected " + type + " got " + var.type);
        }
    }

    public void set(String val,String ty){
        if(!(constant && !value.equals("")) && (type.equals("") || type.equals(ty))){
            value=val;
        } else {
            if(constant) throw new RuntimeException("variable is constant");
            throw new RuntimeException("expected " + type + " got " + ty);
        }
    }

    public boolean canSet(String val){
        if(constant && !value.equals("")) return false;
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

    public Token toToken(){
        return new Token(value, switch (getType(value)){
            case "str" -> Token.Type.STRING;
            case "num" -> Token.Type.NUMBER;
            case "bool" -> Token.Type.BOOL;
            default -> Token.Type.IDENTIFIER;
        });
    }

    private void check(){
        if(!type.equals("") && !value.equals("") && !(type.equals("class") && value.startsWith("§")) && !getType(value).equals(type)) throw new RuntimeException("expected type " + type + " got " + getType(value));
    }

    public static String getType(String value){
        if(value.startsWith("\"")&&value.endsWith("\"")) return "str";
        if(value.matches("[0-9]+(.[0-9]+)?")) return "num";
        if(value.equals("")) return "";
        if(value.startsWith("§§§")) return "§";
        if(value.startsWith("§")) return value.substring(1).split("§")[0];
        return "enum";
    }
}
