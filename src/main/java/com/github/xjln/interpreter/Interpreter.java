package com.github.xjln.interpreter;

import com.github.xjln.lang.Variable;
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
                    execute(line);
                }catch (RuntimeException e){
                    throw new RuntimeException(e.getMessage() + ", in: " + line);
                }
            }
        }catch (FileNotFoundException ignored){} //won't throw
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
            Variable v = getVar(th.current().s());

            if(v != null) throw new RuntimeException("illegal argument");
            th.assertToken("=");



        }else{
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
        throw new RuntimeException("no definition for " + operator.s() + " with " + left.t().toString() + " and " + right.t().toString());
    }
}
