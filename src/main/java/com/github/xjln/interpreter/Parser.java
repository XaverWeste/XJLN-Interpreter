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

    public AST createAST(Tokenhandler th){
        AST.Operation ast = new AST.Operation();
        ast.token = th.next();

        AST.Operation last;
        Token op;

        while (th.hasNext()){
            op = th.assertToken(Token.Type.OPERATOR);
            last = ast;
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
            else if(line.startsWith("use")) sb.append(parseFile(line));
            else if(!line.equals("") && !line.startsWith("#")) throw new RuntimeException("illegal argument in \"" + line +"\"");
        }

        return sb.toString();
    }

    private String parseFile(String current) throws FileNotFoundException {
        Tokenhandler th = new Tokenhandler(scanner.getTokens(current));
        th.assertToken("use");
        th.assertToken(Token.Type.IDENTIFIER);
        File file = new File("src/test/java/" + current.split(" ")[1] + ".xjln");
        if(!file.exists()) throw new RuntimeException("file with path: " + file.getPath() + " does not exist");
        return parseFile(file);
    }

    private void parseClassDef(java.util.Scanner sc, String current){
        Tokenhandler th = new Tokenhandler(scanner.getTokens(current));
        th.assertToken("def");
        String name = th.assertToken(Token.Type.IDENTIFIER).s();
        th.assertToken("[");
        ParameterList pl = parseParameterList(th.getInBracket());
        Class c = new Class(pl);
        current = sc.nextLine().trim();

        while (!current.equals("end")){
            if(current.startsWith("def")) parseMethodDef(sc, current, c);
            else if(current.startsWith("native")) parseMethodDef(current, c, name);
            else if(!current.equals("") && !current.startsWith("#")) throw new RuntimeException("illegal argument in: " + current);
            current = sc.nextLine().trim();
        }

        System.MEM.set(name, c);
        if(pl == null){
            System.MEM.set("§" + name, c.createObject());
            System.MEM.set(name, new Variable(name, "§" + name, true));
        }
    }

    private void parseMethodDef(java.util.Scanner sc, String current, Class c){
        Tokenhandler th = new Tokenhandler(scanner.getTokens(current));
        th.assertToken("def");
        String name = th.assertToken(Token.Type.IDENTIFIER, Token.Type.OPERATOR).s();
        th.assertToken("(");
        ParameterList pl = parseParameterList(th.getInBracket());
        if(pl == null) throw new RuntimeException("tried to define " + name + "() as static");
        Method m = c.mem.getM(name);
        if(m == null){
            m = new Method();
            c.mem.set(name, m);
        }
        m.add(pl, getContent(sc));
    }

    private void parseMethodDef(String current, Class c, String classname){
        Tokenhandler th = new Tokenhandler(scanner.getTokens(current));
        th.assertToken("native");
        String name = th.assertToken(Token.Type.IDENTIFIER).s();
        th.assertToken("(");
        ParameterList pl = parseParameterList(th.getInBracket());
        if(pl == null) throw new RuntimeException("tried to define " + name + "() as static");
        c.mem.set(name, System.getNativeMethod(classname, name, pl));
    }

    private ParameterList parseParameterList(Tokenhandler th){
        if(th.isEmpty()) return new ParameterList();
        if(th.next().s().equals("/")) return null;
        ParameterList pl = new ParameterList();
        Token t = th.current();
        Variable v;
        while (th.hasNext()){
            if(!Set.of("var", "bool", "num", "str", "const").contains(t.s())) throw new RuntimeException("illegal argument");
            v = new Variable(t.s().equals("var") || t.s().equals("const") ?"":t.s());
            t = th.assertToken(Token.Type.IDENTIFIER);
            pl.addParameter(t.s(), v);
            if(th.hasNext()){
                th.assertToken(",");
                t = th.next();
            }
        }
        return pl;
    }

    private String getContent(java.util.Scanner sc){
        StringBuilder sb = new StringBuilder();
        String line;
        boolean breaked = false;

        while(sc.hasNextLine()){
            line = sc.nextLine().trim();
            if(line.equals("end")){
                breaked = true;
                break;
            }

            if(line.startsWith("if") && line.split(" ")[0].equals("if")){
                sb.append(line);
                String content = getContent(sc);
                String[] cases = content.split("else");
                for(String s:cases[0].split("\n")) sb.append("#").append(s);
                for(int i = 1;i < cases.length;i++){
                    sb.append("##else");
                    for(String s:cases[i].split("\n")) if(!s.equals("")) sb.append(sb.toString().endsWith("else") && s.trim().startsWith("if")?"":"#").append(s);
                }
                sb.append("\n");
            }else if(line.startsWith("while") && line.split(" ")[0].equals("while")){
                sb.append(line);
                String content = getContent(sc);
                for(String s:content.split("\n")) sb.append("#").append(s);
                sb.append("\n");
            }else if(!line.startsWith("#")) sb.append(line).append("\n");
        }

        if(!breaked) throw new RuntimeException("end expected");

        return sb.toString();
    }
}
