package com.github.xjln.interpreter;

import com.github.xjln.lang.Class;
import com.github.xjln.lang.ParameterList;
import com.github.xjln.lang.Variable;
import com.github.xjln.system.System;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("All")
class Parser {

    public final Scanner scanner;
    public static final String LIBPATH = "src/main/java/com/github/xjln/";
    private final ArrayList<String> used;

    public Parser(){
        scanner = new Scanner();
        used = new ArrayList<>();
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
            if(!line.startsWith("#")){
                if(line.startsWith("use")){
                    AST[] result = parseFile(file, line);
                    if(result != null) Collections.addAll(main, result);
                }else if(line.startsWith("main")){
                    AST[] result = parseMain(sc, line);
                    if(result != null) Collections.addAll(main, result);
                }else if(line.startsWith("def")) parseClassDef(sc, line);
                else throw new RuntimeException("illegal argument in: " + line);
            }
        }

        return main.toArray(new AST[0]);
    }

    private AST[] parseFile(File from, String line) throws FileNotFoundException {
        String[] args = line.split(" ");
        if(!args[0].equals("use")) throw new RuntimeException("illegal argument in: " + line);
        if(args.length != 2) throw new RuntimeException("expected 2 arguments, got " + args.length);
        File file = new File(args[1].startsWith("lib/") ? LIBPATH + args[1] : args[1]);
        if(!file.exists()) throw new RuntimeException("file " + file.getPath() + " does not exist");
        return parseFile(file);
    }

    private AST[] parseMain(java.util.Scanner sc, String line){
        TokenHandler th = new TokenHandler(scanner.getTokens(line));
        th.assertToken("main");
        if(th.hasNext()){
            th.assertToken("->");
            return new AST[]{parseStatement(th)};
        }
        return parseConent(sc);
    }

    private void parseClassDef(java.util.Scanner sc, String line){
        TokenHandler th = new TokenHandler(scanner.getTokens(line));
        th.assertToken("def");
        th.assertToken(Token.Type.IDENTIFIER);
        String name = th.current().s();
        th.assertToken("[", "=");
        if(System.MEM.getC(name) == null){
            if(th.current().s().equals("[")){
                Class c = new Class(parseParameterList(th.getInBracket()));
            }else{
                //TODO enums
            }
        }else java.lang.System.out.println("[waring]: " + name + " is already defined");
    }

    private ParameterList parseParameterList(TokenHandler th){
        ParameterList pl = new ParameterList();

        String type, name;

        while (th.hasNext()){
            th.assertToken(Token.Type.IDENTIFIER);
            type = th.current().s();
            th.assertToken(Token.Type.IDENTIFIER);
            name = th.current().s();

            pl.addParameter(name, new Variable(type));
        }

        return pl;
    }

    private AST.Statement parseStatement(TokenHandler th){
        return null;
    }

    private AST[] parseConent(java.util.Scanner sc){
        return null;
    }
}
