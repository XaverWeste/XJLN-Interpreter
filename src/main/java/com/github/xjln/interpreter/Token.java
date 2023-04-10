package com.github.xjln.interpreter;

public record Token(String s, Type t){

    public enum Type{SIMPLE,IDENTIFIER,NUMBER,STRING,OPERATOR,BOOL}

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Token && ((Token) obj).t == t && ((Token) obj).s.equals(s);
    }

    public static Type toType(String s){
        switch (s){
            case "str" -> { return Type.STRING; }
            case "num" -> { return Type.NUMBER; }
            case "bool" -> { return Type.BOOL; }
            default -> { return Type.IDENTIFIER; }
        }
    }
}
