package com.github.xjln.interpreter;

import java.util.List;

public class Tokenhandler {

    private final List<Token> tokens;
    private int index = 0;

    public Tokenhandler(List<Token> tokens){
        this.tokens = tokens;
    }

    public void assertToken(String s){
        if(index >= tokens.size()) throw new RuntimeException("Expected " + s + " got nothing");
        if(!tokens.get(index).s().equals(s)) throw new RuntimeException("Expected " + s + " got " + tokens.get(index).s());
        index++;
    }

    public Token assertToken(Token.Type t){
        if(index >= tokens.size()) throw new RuntimeException("Expected " + t.toString() + " got nothing");
        if(tokens.get(index).t() != t) throw new RuntimeException("Expected " + t.toString() + " got " + tokens.get(index).t().toString());
        index++;
        return tokens.get(index - 1);
    }

    public Token next(){
        if(index + 1 < tokens.size()) throw new RuntimeException("Expected Token, got nothing");
        index++;
        return tokens.get(index + 1);
    }

    public Token current(){
        return tokens.get(index);
    }

    public Token last(){
        if(index == 0) return tokens.get(0);
        return tokens.get(index - 1);
    }

    public static void assertToken(Token token, String s){
        if(!token.s().equals(s)) throw new RuntimeException("Expected " + s + " got " + token.s());
    }

    public static Token assertToken(Token token, Token.Type t){
        if(token.t() != t) throw new RuntimeException("Expected " + t.toString() + " got " + token.t().toString());
        return token;
    }
}
