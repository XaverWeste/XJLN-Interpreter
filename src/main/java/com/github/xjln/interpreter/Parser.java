package com.github.xjln.interpreter;

import com.github.xjln.lang.*;
import com.github.xjln.lang.Class;
import com.github.xjln.lang.Enum;
import com.github.xjln.system.System;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class Parser {

    public final Lexer lexer;
    private Scanner sc;
    private HashMap<String, String> uses;
    private Class currentClass = null;
    private Enum currentEnum = null;
    private String className;

    public Parser(){
        lexer = new Lexer();
    }

    public void parseFile(String path){
        uses = new HashMap<>();

        try {
            sc = new java.util.Scanner(new File(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file " + path + " not found");
        }

        path = path.substring(0, path.length() - 5);

        String line;
        while (sc.hasNextLine()) {
            line = sc.nextLine().trim();
            if (!line.equals("") && !line.startsWith("#")) {
                if (line.startsWith("use")) parseUseDef(line);
                else if(line.startsWith("def")){
                    parseDef(line);
                    if(currentClass != null) {
                        System.MEM.set(path + "/" + className, currentClass);
                        currentClass = null;
                    }else{
                        System.MEM.set(path + "/" + className, currentEnum);
                        currentEnum = null;
                    }
                    use(path + "/" + className, className);
                } else throw new RuntimeException("illegal argument in: " + line);
            }
        }
    }

    private void parseUseDef(String line){
        TokenHandler th = lexer.getTokens(line);
        th.assertToken("use");

        String from = null, as = null;
        ArrayList<String> use = new ArrayList<>();

        use.add(th.assertToken(Token.Type.IDENTIFIER).s());
        while(th.assertToken(Token.Type.IDENTIFIER, Token.Type.OPERATOR).equals("/")){
            use.add(th.assertToken(Token.Type.IDENTIFIER).s());
        }

        if(th.current().equals("from")){
            StringBuilder path = new StringBuilder();
            path.append(th.assertToken(Token.Type.IDENTIFIER));
            while(th.hasNext() && th.assertToken(Token.Type.OPERATOR).s().equals("/"))
                path.append("/").append(th.assertToken(Token.Type.IDENTIFIER));
            from = path.toString();
        }

        if(th.current().equals("as"))
            as = th.assertToken(Token.Type.IDENTIFIER).s();

        th.assertNull();

        if(as != null){
            if(use.size() != 1)
                throw new RuntimeException("only can alias one in: " + line);
            else
                use(from != null ? from + "/" + use.get(0) : use.get(0), as);
        }else{
            for(String s:use)
                use(from != null ? from + "/" + s : s, null);
        }
    }

    private void use(String validName, String name){
        if(name == null)
            name = validName.split("/")[validName.split("/").length - 1]; //TODO

        if(uses.containsKey(name)) throw new RuntimeException(name + " is already used in");

        uses.put(name, validName);
    }

    private void parseDef(String line){
        TokenHandler th = lexer.getTokens(line);
        th.assertToken("def");
        className = th.assertToken(Token.Type.IDENTIFIER).s();

        if(th.assertToken("[", "=").equals("="))
            parseEnumDef(th);
        else
            parseClassDef(th);
    }

    private void parseEnumDef(TokenHandler th){
        ArrayList<String> values = new ArrayList<>();

        while (th.hasNext()){
            values.add(th.assertToken(Token.Type.IDENTIFIER).s());
            if(th.hasNext()){
                th.assertToken(",");
                th.assertHasNext();
            }
        }

        currentEnum = new Enum(values.toArray(new String[0]));
    }

    private void parseClassDef(TokenHandler th){
        ParameterList pl = parseParameterList(th.getInBracket());
        String constructor = null;
        ArrayList<String> superClasses = new ArrayList<>();

        if(th.hasNext()){
            if(th.assertToken("=>", "->").equals("->")){
                constructor = th.assertToken(Token.Type.IDENTIFIER).s(); //TODO
                if(th.hasNext()) th.assertToken("=>");
            }

            if(th.current().equals("=>")){
                th.assertHasNext();
                while (th.hasNext()) {
                    superClasses.add(th.assertToken(Token.Type.IDENTIFIER).s());
                    if (th.hasNext()) {
                        th.assertToken(",");
                        th.assertHasNext();
                    }
                }
            }
        }

        currentClass = new Class(pl, constructor, superClasses.toArray(new String[0]));

        String line;
        while (sc.hasNextLine()) {
            line = sc.nextLine().trim();
            if (!line.equals("") && !line.startsWith("#")) {
                if(line.startsWith("def ")) parseMethodDef(line);
                else parseFieldDef(line);
            }
        }
    }

    private void parseFieldDef(String line){
        TokenHandler th = lexer.getTokens(line);
        String type = th.assertToken(Token.Type.IDENTIFIER).s();
        Variable v = type.equals("var") ? new Variable() : new Variable(type);
        String name = th.assertToken(Token.Type.IDENTIFIER).s();
        currentClass.mem.set(name, v);
    }

    private void parseMethodDef(String line){
        TokenHandler th = lexer.getTokens(line);
        th.assertToken("def");
        String name = th.assertToken(Token.Type.IDENTIFIER, Token.Type.OPERATOR).s();
        th.assertToken("(");
        ParameterList pl = parseParameterList(th.getInBracket());
        th.assertNull();

        Method m = currentClass.mem.getM(name);

        if(m == null){
            m = new Method();
            currentClass.mem.set(name, m);
        }

        m.add(pl, parseAST());
    }

    private ParameterList parseParameterList(TokenHandler th){
        ParameterList pl = new ParameterList();

        while (th.hasNext()){
            String type = th.assertToken(Token.Type.IDENTIFIER).s();
            Variable v = type.equals("var") ? new Variable() : new Variable(type);
            String name = th.assertToken(Token.Type.IDENTIFIER).s();

            pl.addParameter(name, v);
        }

        return pl;
    }

    private AST[] parseAST(){
        ArrayList<AST> ast = new ArrayList<>();

        String line;
        while (sc.hasNextLine()){
            line = sc.nextLine();
            if(line.equals("end"))
                return ast.toArray(new AST[0]);

            if(!line.startsWith("#") && !line.equals("")){
                if(line.startsWith("if ")){
                    //TODO
                }else if(line.startsWith("while ")){
                    AST.While w = new AST.While();
                    TokenHandler th = lexer.getTokens(line);
                    th.assertToken("while");
                    w.condition = parseCalc(th);
                    w.content = parseAST();
                    ast.add(w);
                }else{

                }
            }
        }

        return ast.toArray(new AST[0]);
    }

    private AST.Calc parseCalc(TokenHandler th){
        if(th.size() == 0) throw new RuntimeException("expected argument, got nothing");
        AST.Calc calc = new AST.Calc();

        if(th.assertToken(Token.Type.IDENTIFIER, Token.Type.OPERATOR, Token.Type.STRING, Token.Type.NUMBER).t() == Token.Type.IDENTIFIER)
            calc.call = parseNext(th);
        else
            calc.content = th.current();

        if(th.hasNext()){
            AST.Calc current = calc;
            calc = new AST.Calc();
            calc.left = current;

            calc.content = th.assertToken(Token.Type.OPERATOR);
            calc.right = parseCalc(th);
        }

        return calc;
    }

    private AST parseNext(TokenHandler th){
        Token name = th.current();
        if(th.assertToken(":", "(").equals(":")){
            AST.RefCall call = new AST.RefCall();
            call.name = name.s();
            AST next = parseNext(th);

            if(next instanceof AST.RefCall)
                call.next = (AST.RefCall) next;
            else if(next instanceof AST.MethodCall)
                call.call = (AST.MethodCall) next;
        }else{
            //TODO
        }
        return null;
    }
}
