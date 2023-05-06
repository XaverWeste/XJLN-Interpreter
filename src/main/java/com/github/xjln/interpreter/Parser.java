package com.github.xjln.interpreter;

import com.github.xjln.lang.Class;
import com.github.xjln.lang.Enum;
import com.github.xjln.lang.Method;
import com.github.xjln.lang.ParameterList;
import com.github.xjln.lang.Variable;
import com.github.xjln.system.Memory;
import com.github.xjln.system.System;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        if(!System.validateName(name)) throw new RuntimeException("illegal classname in " + line);
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

        while(sc.hasNextLine()) {
            line = sc.nextLine().trim();
            if (!line.startsWith("#") && !line.equals("")) {
                if(line.startsWith("def")) parseMethodDef(sc, line, c.mem);
                else throw new RuntimeException("illegal argument in " + line);
            }
        }

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

        while(sc.hasNextLine()) {
            line = sc.nextLine().trim();
            if (!line.startsWith("#") && !line.equals("")) {
                if(line.startsWith("def")) parseMethodDef(sc, line, e.mem);
                else throw new RuntimeException("illegal argument in " + line);
            }
        }

        if(System.MEM.exist(name)) throw new RuntimeException("instance already exist in " + line);
        System.MEM.set(name, e);
    }

    private void parseMethodDef(java.util.Scanner sc, String line, Memory.ClassMemory mem){
        TokenHandler th = new TokenHandler(scanner.getTokens(line));
        th.assertToken("def");
        String name = th.assertToken(Token.Type.IDENTIFIER).s();
        th.assertToken("(");
        ParameterList pl;

        try{
            pl = parseParameterList(th.getInBracket());
        }catch (RuntimeException runtimeException){
            RuntimeException exception = new RuntimeException(runtimeException.getMessage() + " in " + line);
            exception.setStackTrace(runtimeException.getStackTrace());
            throw exception;
        }

        Method m = mem.getM(name);
        if(m == null){
            m = new Method();
            mem.set(name, m);
        }else if(m.getCode(pl) != null) throw new RuntimeException("Method " + name + " is already defined in " + line);

        List<AST> ast = new ArrayList<>();
        if(th.hasNext()){
            th.assertToken(":"); // TODO
            ast.add(new AST.Statement(th.assertToken(Token.Type.IDENTIFIER).s() + " result"));
        }

        ast.addAll(parseContent(sc));
        m.add(pl, ast.toArray(new AST[0]));
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

    private List<AST> parseContent(java.util.Scanner sc){
        List<AST> result = new ArrayList<>();
        int i = 1;
        String line;

        while (i > 0 && sc.hasNextLine()){
            line = sc.nextLine().trim();
            if(!line.startsWith("#") && !line.equals("")){
                if(line.startsWith("end")){
                    if(!line.equals("end")) throw new RuntimeException("illegal argument in " + line);
                    i--;
                }else result.add(parseAST(sc, line));
            }
        }

        if(i > 0) throw new RuntimeException("Method was not closed");
        return result;
    }

    private AST parseAST(java.util.Scanner sc, String line){
        switch (line.split(" ")[0]){
            case "if" -> {
                return parseIf(sc, line);
            }
            case "while" -> {
                return parseWhile(sc, line);
            }
            default -> throw new RuntimeException("illegal argument in " + line);
        }
    }

    private AST.IfBranch parseIf(java.util.Scanner sc, String line){
        //TODO
        return null;
    }

    private AST.WhileLoop parseWhile(java.util.Scanner sc, String line){
        //TODO
        return null;
    }

    private AST.Call parseCall(TokenHandler th){
        //TODO
        return null;
    }

    private AST.Calculation parseCalculation(TokenHandler th){
        //TODO
        return null;
    }
}
