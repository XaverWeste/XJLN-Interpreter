package com.github.xjln.interpreter;

import com.github.xjln.lang.Variable;
import com.github.xjln.system.System;

import java.io.File;
import java.io.FileNotFoundException;

public class Interpreter {

    private final Parser parser;

    public Interpreter(){
        parser = new Parser();
    }

    public void execute(File file){
        if(!file.exists()) throw new RuntimeException("file with path: " + file.getPath() + " does not exist");

        try{
            for(String line:parser.parseFile(file).split("\n")){
                try{
                    execute(line);
                }catch (RuntimeException e){
                    throw new RuntimeException(e.getMessage() + ", in: " + line);
                }
            }
        }catch (FileNotFoundException ignored){} //won't throw

        java.lang.System.out.println("XJLN Process finished successfully");
    }

    private void execute(String line){
        line = line.trim();
        if(!(line.equals("") || line.startsWith("#"))){
            Tokenhandler th = new Tokenhandler(parser.scanner.getTokens(line));
            th.assertToken(Token.Type.IDENTIFIER);

            executeVarAssigment(th);
        }
    }

    private void executeVarAssigment(Tokenhandler th){
        if(th.current().t() == Token.Type.IDENTIFIER){
            String type = th.last().s();
            String name = th.current().s();

            Variable v = getVar(name);
            if(v != null) throw new RuntimeException("illegal argument");

            if(!th.hasNext()) throw new RuntimeException("expected value");
            th.assertToken("=");

            v = new Variable(type, parser.createAST(th).execute(this).s(), type.equals("const"));
            System.mem.set(name, v);
        }else{
            if(!th.hasNext()) throw new RuntimeException("expected value");
            th.assertToken("=");
            Variable v = getVar(th.last().s());

            if(v == null){
                v = new Variable();
                System.mem.set(th.last().s(), v);
            }

            Token value = parser.createAST(th).execute(this);
            v.set(value.s(), Variable.getType(value.t().toString()));
        }
    }

    private Variable getVar(String name){
        return System.mem.get(name);
    }

    public Token executeOperation(Token left, Token operator, Token right){
        Tokenhandler.assertToken(operator, Token.Type.OPERATOR);
        switch(left.t()){
            case NUMBER -> {
                Tokenhandler.assertToken(right, Token.Type.NUMBER);
                double first = Double.parseDouble(left.s());
                double second = Double.parseDouble(right.s());
                switch (operator.s()){
                    case "+" -> { return new Token(String.valueOf(first + second), Token.Type.NUMBER); }
                    case "-" -> { return new Token(String.valueOf(first - second), Token.Type.NUMBER); }
                    case "*" -> { return new Token(String.valueOf(first * second), Token.Type.NUMBER); }
                    case "/" -> { return new Token(String.valueOf(first / second), Token.Type.NUMBER); }
                    case "%" -> { return new Token(String.valueOf(first % second), Token.Type.NUMBER); }
                    case "==" -> { return new Token(String.valueOf(first == second), Token.Type.BOOL); }
                    case "!=" -> { return new Token(String.valueOf(first != second), Token.Type.BOOL); }
                }
            }
            case BOOL -> {
                Tokenhandler.assertToken(right, Token.Type.BOOL);
                boolean first = Boolean.parseBoolean(left.s());
                boolean second = Boolean.parseBoolean(right.s());
                switch (operator.s()){
                    case "==" -> { return new Token(String.valueOf(first == second), Token.Type.BOOL); }
                    case "!=" -> { return new Token(String.valueOf(first != second), Token.Type.BOOL); }
                    case "&" -> { return new Token(String.valueOf(first && second), Token.Type.BOOL); }
                    case "|" -> { return new Token(String.valueOf(first || second), Token.Type.BOOL); }
                }
            }
            case STRING -> {
                if(operator.s().equals("+")){
                    if(right.t() == Token.Type.STRING) return new Token(left.s().substring(1, left.s().length() - 1) + right.s().substring(1, right.s().length() - 1), Token.Type.STRING);
                    else return new Token(left.s().substring(1, left.s().toCharArray().length) + right.s(), Token.Type.STRING);
                }
                else{
                    switch (operator.s()){
                        case "==" -> { return new Token(String.valueOf(left.s().equals(right.s())), Token.Type.BOOL); }
                        case "!=" -> { return new Token(String.valueOf(!left.s().equals(right.s())), Token.Type.BOOL); }
                    }
                }
            }
        }
        throw new RuntimeException("no definition for " + operator.s() + " with " + left.t().toString() + " and " + right.t().toString());
    }
}
