package com.github.xjln.interpreter;

import com.github.xjln.lang.*;
import com.github.xjln.lang.Class;
import com.github.xjln.system.System;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

public class Parser {

    public final Scanner scanner;

    public Parser(){
        scanner = new Scanner();
    }

    public AST createAST(TokenHandler th){
        AST.Operation ast = new AST.Operation();
        ast.token = th.next();

        Token op;

        while(th.hasNext()){
            op = th.assertToken(Token.Type.OPERATOR);
            if(!th.hasNext()) throw new RuntimeException("illegal argument");
            AST.Operation last = ast;
            ast = new AST.Operation();
            ast.left = last;
            ast.token = op;
            ast.right = new AST.Operation();
            ast.right.token = th.next();
        }

        return ast;
    }

    public String parseFile(File file) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        java.util.Scanner sc = new java.util.Scanner(file);
        String line;

        while(sc.hasNextLine()){
            line = sc.nextLine().trim();
            if(line.equals("main")) sb.append(getContent(sc));
            else if(line.startsWith("def")) parseClassDef(sc, line);
            else if(!line.equals("")) throw new RuntimeException("illegal argument in \"" + line +"\"");
        }

        return sb.toString();
    }

    private void parseClassDef(java.util.Scanner sc, String current){
        TokenHandler th = new TokenHandler(scanner.getTokens(current));
        th.assertToken("def");
        String name = th.assertToken(Token.Type.IDENTIFIER).s();
        Class c;
        if(name.equals("native")){
            name = th.assertToken(Token.Type.IDENTIFIER).s();
            th.assertToken("[");
            th.assertToken("]");
            switch (name){
                case "System" -> c = new com.github.xjln.nativ.System();
                default -> throw new RuntimeException("class " + name + " is not defined natively");
            }
        }else{
            th.assertToken("[");
            c = new Class(parseParameterList(th));
            current = sc.nextLine().trim();
            while (!current.equals("end")){
                if(current.startsWith("def")) parseMethodDef(sc, current, c);
                else if(!current.equals("") && !current.startsWith("#")) throw new RuntimeException("illegal argument in: " + current);
                current = sc.nextLine().trim();
            }
        }

        System.MEM.set(name, c);
    }

    private void parseMethodDef(java.util.Scanner sc, String current, Class c){
        TokenHandler th = new TokenHandler(scanner.getTokens(current));
        th.assertToken("def");
        String name = th.assertToken(Token.Type.IDENTIFIER).s();
        th.assertToken("(");
        c.mem.set(name, new Method(parseParameterList(th), getContent(sc)));
    }

    private ParameterList parseParameterList(TokenHandler th){
        ParameterList pl = new ParameterList();
        String end = th.last().s().equals("(")?")":"]";
        Token t = th.current();
        Variable v;
        while (!t.s().equals(end)){
            if(!Set.of("var", "bool", "num", "str").contains(t.s())) throw new RuntimeException("illegal argument");
            v = new Variable(t.s().equals("var")?"":t.s());
            th.next();
            t = th.assertToken(Token.Type.IDENTIFIER);
            pl.addParameter(t.s(), v);
            t = th.next();
            if(t.s().equals(end)) break;
            if(!t.s().equals(",")) throw new RuntimeException("expected comma got " + t.s());
            t = th.next();
        }
        return pl;
    }

    private String getContent(java.util.Scanner sc){
        StringBuilder sb = new StringBuilder();
        String line;
        int i = 1;

        while(sc.hasNextLine() && i > 0){
            line = sc.nextLine().trim();
            if(line.startsWith("end")){
                if(!line.equals("end")) throw new RuntimeException();
                i--;
            }
            if(i > 0) sb.append(line).append("\n");
        }

        if(i > 0) throw new RuntimeException("Method was not closed");

        return sb.toString();
    }
}
