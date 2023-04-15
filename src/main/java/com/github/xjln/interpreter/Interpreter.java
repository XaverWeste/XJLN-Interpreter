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
        }catch (FileNotFoundException ignored){} // won't throw

        java.lang.System.out.println("\nXJLN Process finished successfully\n");
    }

    private void execute(String line, Object o, Memory mem){
        line = line.trim();
        if(!line.equals("")){
            Tokenhandler th = new Tokenhandler(parser.scanner.getTokens(line));
            executeStatement(th, o, mem);
        }
    }

    private Token executeStatement(Tokenhandler th, Object o, Memory mem){
        List<Token> tokens = new ArrayList<>();

        while (th.hasNext()) tokens.add((th.next().t() == Token.Type.IDENTIFIER || th.current().s().equals("(")) ? executeNext(th, o, mem).toToken() : th.current());

        return parser.createAST(new Tokenhandler(tokens)).execute(this);
    }

    private Variable executeNext(Tokenhandler th, Object o, Memory mem){
        Variable var;
        if (th.hasNext() && th.isValid() && (th.current().t() == Token.Type.IDENTIFIER || th.current().s().equals("("))) {
            if(th.current().s().equals("(")){
                Tokenhandler tokenhandler = th.getInBracket();
                Token t = executeStatement(tokenhandler, o, mem);
                var = new Variable(Variable.getType(t.s()), t.s(), false);
            }else if (th.next().s().equals("(")) {
                th.last();
                executeMethod(th, o, mem);
                var = System.MEM.get("result");
            } else if (th.current().s().equals("[")) {
                th.last();
                String obj = executeClass(th, o, mem);
                var = new Variable(Variable.getType(obj), obj, false);
            } else {
                if(th.current().t() == Token.Type.IDENTIFIER){
                    if(th.last().t() == Token.Type.IDENTIFIER){
                        if(getVar(th.next().s(), o, mem) != null) throw new RuntimeException("variable " + th.current().s() + " already exist");
                        else{
                            var = new Variable(th.last().s().equals("var") ||th.current().s().equals("const") ? "" : th.current().s(), "", th.current().s().equals("const"));
                            setVar(th.next().s(), var, o, mem);
                        }
                    }else{
                        var = getVar(th.current().s(), o, mem);
                        if (var == null){
                            var = new Variable();
                            setVar(th.current().s(), var, o, mem);
                        }
                    }
                }else{
                    var = getVar(th.last().s(), o, mem);
                    if (var == null){
                        var = new Variable();
                        setVar(th.current().s(), var, o, mem);
                    }
                }
            }
            if (var != null) {
                if(!th.hasNext()) return var;
                if(th.next().s().equals(":")){
                    th.next();
                    if (!var.value().startsWith("§")) throw new RuntimeException("object expected");
                    return executeNext(th, System.MEM.getO(var.value()), null);
                } else if(th.current().s().equals("=")) {
                    //th.next();
                    //var.set(executeNext(th, o, mem));
                    Token t = executeStatement(th, o, mem);
                    var.set(t.s(), Variable.getType(t.s()));
                    return var;
                } else {
                    th.last();
                    return var;
                }
            } else throw new RuntimeException("illegal argument");
        } else {
            if(th.current().t() == Token.Type.IDENTIFIER) {
                var = getVar(th.current().s(), o, mem);
                if (var == null) throw new RuntimeException("Variable " + th.current().s() + " does not exist");
                return var;
            } else return new Variable(Variable.getType(th.current().s()), th.current().s(), false);
        }
    }

    private void executeMethod(Tokenhandler th, Object o, Memory mem){
        String name = th.current().s();
        th.next();
        String[] paras = getParas(th.getInBracket(), o, mem);

        Method m = o.clas.mem.getM(name);
        if (m == null) throw new RuntimeException("method didn't exist");

        if(m instanceof NativeMethod){
            if(!((NativeMethod) m).pl.matches(paras)) throw new RuntimeException("illegal argument");
            mem = ((NativeMethod) m).pl.createMem(paras);
            ((NativeMethod)m).code.execute(o, mem);
        } else {
            ParameterList pl = m.getPl(paras);
            mem = pl.createMem(paras);
            for (String l : m.getCode(pl).split("\n")) execute(l, o, mem);
        }
    }

    private String executeClass(Tokenhandler th, Object o, Memory mem){
        String name = th.current().s();
        Class c = System.MEM.getC(name);
        if(c == null) throw new RuntimeException("class " + name + " didn't exist");

        th.next();
        String[] paras = getParas(th.getInBracket(), o, mem);
        if(!c.pl.matches(paras)) throw new RuntimeException("illegal argument");

        Object obj = c.createObject();
        obj.mem.add(c.pl.createMem(paras));
        name = System.createName(name);
        System.MEM.set(name, obj);

        return name;
    }

    private String[] getParas(Tokenhandler th, Object o, Memory mem){
        if(!th.hasNext()) return new String[0];
        ArrayList<String> values = new ArrayList<>();
        Token token;
        ArrayList<Token> current = new ArrayList<>();

        while (th.hasNext()){
            token = th.next();
            current.add(token);
            if(th.hasNext()) {
                if (th.next().s().equals(",")) {
                    values.add(executeStatement(new Tokenhandler(current), o, mem).s());
                    current = new ArrayList<>();
                } else th.last();
            }
        }

        if(!current.isEmpty()) values.add(executeStatement(new Tokenhandler(current), o, mem).s());

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
                    Tokenhandler.assertToken(right, Token.Type.STRING);
                    switch (operator.s()){
                        case "==" -> { return new Token(String.valueOf(left.s().equals(right.s())), Token.Type.BOOL); }
                        case "!=" -> { return new Token(String.valueOf(!left.s().equals(right.s())), Token.Type.BOOL); }
                    }
                }
            }
            case IDENTIFIER -> {
                Object o = System.MEM.getO(left.s());
                if(o == null) throw new RuntimeException("object " + left.s() + " does not exist");
                Method m = o.clas.mem.getM(operator.s());
                if(m != null){
                    String[] paras = new String[]{left.s(), right.s()};
                    ParameterList pl = m.getPl(paras);
                    if(pl != null){
                        String result = System.MEM.get("result").value();
                        Memory mem = pl.createMem(paras);
                        for (String l : m.getCode(pl).split("\n")) execute(l, o, mem);
                        if(System.MEM.get("result").value().equals(result)) throw new RuntimeException("expected result");
                        return System.MEM.get("result").toToken();
                    }
                }
            }
        }
        throw new RuntimeException("no definition for " + operator.s() + " with " + left.t().toString() + " and " + right.t().toString());
    }
}
