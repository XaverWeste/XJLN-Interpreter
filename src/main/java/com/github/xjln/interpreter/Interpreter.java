package com.github.xjln.interpreter;

import com.github.xjln.lang.*;
import com.github.xjln.lang.Class;
import com.github.xjln.lang.Object;
import com.github.xjln.system.Memory;
import com.github.xjln.system.System;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
                    execute(line, null, null);
                }catch (RuntimeException e){
                    RuntimeException exception = new RuntimeException(e.getMessage() + ", in: " + line);
                    exception.setStackTrace(e.getStackTrace());
                    throw exception;
                }
            }
        }catch (FileNotFoundException ignored){} //won't throw

        java.lang.System.out.println("\nXJLN Process finished successfully\n");
    }

    private void execute(String line, Object o, Memory mem){
        line = line.trim();
        if(!(line.equals("") || line.startsWith("#"))){
            TokenHandler th = new TokenHandler(parser.scanner.getTokens(line));
            th.assertToken(Token.Type.IDENTIFIER);

            switch(th.current().s()){
                case "(" -> executeMethod(th, o, mem);
                case "[" -> executeClass(th, o, mem);
                case ":" ->{
                    th.back();
                    getVar(th, o, mem);
                }
                default -> executeVarAssigment(th, o, mem);
            }
        }
    }

    private void executeVarAssigment(TokenHandler th, Object o, Memory mem){
        if(th.current().t() == Token.Type.IDENTIFIER){
            String type = th.last().s();
            String name = th.next().s();

            Variable v = getVar(name, o, mem);
            if(v != null) throw new RuntimeException("illegal argument");

            if(!th.hasNext()) throw new RuntimeException("expected value");
            th.assertToken("=");

            v = new Variable(type, executeStatement(th, o, mem).s(), type.equals("const"));
            setVar(name, v, o, mem);
        }else{
            if(!th.hasNext()) throw new RuntimeException("expected value");
            th.assertToken("=");
            th.back();
            Variable v = getVar(th.last().s(), o, mem);

            if(v == null){
                v = new Variable();
                setVar(th.last().s(), v, o, mem);
            }

            th.next();

            Token value = executeStatement(th, o, mem);
            v.set(value.s(), Variable.getType(value.s()));
        }
    }

    private Token executeStatement(TokenHandler th, Object o, Memory mem){
        List<Token> tokens = new ArrayList<>();

        while (th.hasNext()) tokens.add((th.next().t() == Token.Type.IDENTIFIER) ? getVar(th, o, mem).toToken() : th.last());

        return parser.createAST(new TokenHandler(tokens)).execute(this);
    }

    private Variable getVar(TokenHandler th, Object o, Memory mem){
        Variable var;
        if(th.hasNext()) {
            if (th.current().s().equals("(")) {
                executeMethod(th, o, mem);
                var = System.MEM.get("result");
            } else if (th.current().s().equals("[")) {
                String clas = executeClass(th, o, mem);
                return new Variable(clas.substring(1).split("§")[0], clas,false);
            } else {
                var = getVar(th.last().s(), o, mem);
                if (var == null) throw new RuntimeException("Variable " + th.current().s() + " does not exist");
            }
            if(var != null) {
                if(!th.hasNext()) return var;
                th.next();
                if (!th.current().s().equals(":")){
                    th.back();
                    return var;
                }
                else {
                    th.next();
                    th.next();
                    if (!var.value().startsWith("§")) throw new RuntimeException("object expected");
                    else return getVar(th, System.MEM.getO(var.value()), null);
                }
            }
        }else{
            var = getVar(th.current().s(), o, mem);
            if (var == null) throw new RuntimeException("Variable " + th.current().s() + " does not exist");
            return var;
        }
        throw new RuntimeException("illegal argument");
    }

    private void executeMethod(TokenHandler th, Object o, Memory mem){
        String methodName = th.last().s();
        String[] paras = getParas(th, o, mem);

        Method m = o.clas.mem.getM(methodName);
        if (m == null) throw new RuntimeException("method didn't exist");

        if (!m.pl.matches(paras)) throw new RuntimeException("illegal argument");
        mem = m.pl.createMem(paras);

        if(m instanceof NativeMethod) ((NativeMethod)m).code.execute(o, mem);
        else for (String l : m.code.split("\n")) execute(l, o, mem);
    }

    private String executeClass(TokenHandler th, Object o, Memory mem) {
        String name = th.last().s();
        Class c = System.MEM.getC(name);
        if(c == null) throw new RuntimeException("class didn't exist");

        String[] paras = getParas(th, o, mem);
        if(!c.pl.matches(paras)) throw new RuntimeException("illegal argument ");

        Object obj = c.createObject();
        obj.mem.add(c.pl.createMem(paras));
        name = System.createName(name);
        System.MEM.set(name, obj);

        return name;
    }

    private String[] getParas(TokenHandler th, Object o, Memory mem){
        ArrayList<String> values = new ArrayList<>();
        String end = th.next().s().equals("(")?")":"]";
        Token t = th.next();
        ArrayList<Token> operation = new ArrayList<>();

        while (!t.s().equals(end)){
            if(t.t() == Token.Type.OPERATOR) throw new RuntimeException("expected value");
            operation.add(t);
            t = th.next();
            while(!t.s().equals(end) && !t.s().equals(",")){
                operation.add(t);
                t = th.next();
            }
            values.add(executeStatement(new TokenHandler(operation), o, mem).s());
            if(t.s().equals(end)) break;
            operation = new ArrayList<>();
            t = th.next();
        }

        if(th.hasNext()) th.next();
        return values.toArray(new String[0]);
    }

    private Variable getVar(String name, Object o, Memory mem){
        if(mem != null && mem.exist(name)) return mem.get(name);
        if(o != null && o.mem.exist(name)) return o.mem.get(name);
        return System.MEM.get(name);
    }

    private void setVar(String name, Variable var, Object o, Memory mem){
        if(mem != null && (mem.exist(name) || !(System.MEM.exist(name) || (o != null && o.mem.exist(name))))) mem.set(name, var);
        else if(o != null && (o.mem.exist(name) || !(System.MEM.exist(name)))) o.mem.set(name, var);
        else System.MEM.set(name, var);
    }

    public Token executeOperation(Token left, Token operator, Token right){
        TokenHandler.assertToken(operator, Token.Type.OPERATOR);
        switch(left.t()){
            case NUMBER -> {
                TokenHandler.assertToken(right, Token.Type.NUMBER);
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
                TokenHandler.assertToken(right, Token.Type.BOOL);
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
