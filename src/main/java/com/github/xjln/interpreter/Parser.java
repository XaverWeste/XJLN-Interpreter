package com.github.xjln.interpreter;

import com.github.xjln.lang.Class;
import com.github.xjln.lang.Enum;
import com.github.xjln.lang.Method;
import com.github.xjln.lang.ParameterList;
import com.github.xjln.lang.Variable;
import com.github.xjln.system.System;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

class Parser {

    public final Scanner scanner;
    public final String LIBPATH = "src/main/java/com/github/xjln/";
    private final ArrayList<String> used;

    public Parser(){
        scanner = new Scanner();
        used = new ArrayList<>();
    }

    public void parseFile(String path){
        try{
            parseFile(new File(path));
        }catch (FileNotFoundException e){
            throw new RuntimeException("file " + path + " didn't exist");
        }
    }

    public AST[] parseFile(File file) throws FileNotFoundException {
        if(used.contains(file.getPath())){
            java.lang.System.out.println("[waring]: " + file.getPath() + " is already used");
            return null;
        }else used.add(file.getPath());
        ArrayList<AST> main = new ArrayList<>();

        java.util.Scanner sc = new java.util.Scanner(file);
        String line;

        while(sc.hasNextLine()){
            line = sc.nextLine().trim();
            if(!line.startsWith("#") && !line.equals("")){
                if(line.startsWith("use")){
                    AST[] result = parseFile(file, line);
                    if(result != null) Collections.addAll(main, result);
                }else if(line.startsWith("def")) parseDef(sc, line);
                //TODO
                else throw new RuntimeException("illegal argument in: " + line);
            }
        }

        return main.toArray(new AST[0]);
    }

    private AST[] parseFile(File from, String line) throws FileNotFoundException {
        String[] args = line.split(" ");
        if(!args[0].equals("use")) throw new RuntimeException("illegal argument in: " + line);
        if(args.length != 2) throw new RuntimeException("expected 2 arguments, got " + args.length);
        File file = new File(args[1].startsWith("lib/") ? LIBPATH + args[1] + ".xjln" : args[1] + ".xjln");
        if(!file.exists()) throw new RuntimeException("file " + file.getPath() + " does not exist");
        return parseFile(file);
    }

    private void parseDef(java.util.Scanner sc, String line){
        TokenHandler th = new TokenHandler(scanner.getTokens(line));
        th.assertToken("def");
        String name = th.assertToken(Token.Type.IDENTIFIER).s();
        th.assertToken("=", "[");
        if(th.current().s().equals("[")) parseClassDef(sc, line, th, name);
        else parseEnumDef(sc, line, th, name);
    }

    private void parseClassDef(java.util.Scanner sc, String line, TokenHandler th, String name){
        ParameterList pl;
        try{
            pl = parseParameterList(th.getInBracket());
        }catch (RuntimeException e){
            RuntimeException exception = new RuntimeException(e.getMessage() + " in " + line);
            exception.setStackTrace(e.getStackTrace());
            throw exception;
        }
        Class c = new Class(pl, new String[0]); //TODO Superclasses
        //TODO
        if(c.pl != null){
            if(System.MEM.getC(name) != null) throw new RuntimeException("class already exist in " + line);
            System.MEM.set(name, c);
        }else{
            if(System.MEM.exist(name)) throw new RuntimeException("instance already exist in " + line);
            System.MEM.set(name, new Variable(name, "§" + name, false));
            System.MEM.set("§" + name, c.createObject());
        }
    }

    private void parseEnumDef(java.util.Scanner sc, String line, TokenHandler th, String name){
        Enum e;
        ArrayList<String> values = new ArrayList<>();
        try {
            values.add(th.assertToken(Token.Type.IDENTIFIER).s());
            while(th.hasNext() && th.next().s().equals("|")) values.add(th.assertToken(Token.Type.IDENTIFIER).s());
            e = new Enum(values.toArray(new String[0]));
        }catch (RuntimeException runtimeException){
            RuntimeException exception = new RuntimeException(runtimeException.getMessage() + " in " + line);
            exception.setStackTrace(runtimeException.getStackTrace());
            throw exception;
        }
        //TODO
        if(System.MEM.exist(name)) throw new RuntimeException("instance already exist in " + line);
        System.MEM.set(name, e);
    }

    private ParameterList parseParameterList(TokenHandler th){
        ParameterList pl = new ParameterList();

        if(th.hasNext()){
            if(th.next().s().equals("/")) return null;
            th.toFirst();
        }

        String type, name;

        while (th.hasNext()){
            type = th.assertToken(Token.Type.IDENTIFIER).s();
            name = th.assertToken(Token.Type.IDENTIFIER).s();
            pl.addParameter(name, new Variable(type));
            if(th.hasNext()){
                th.assertToken(",");
                if(!th.hasNext()) throw new RuntimeException("illegal argument");
            }
        }

        return pl;
    }
}
