package com.github.xjln.interpreter;

import com.github.xjln.lang.Class;
import com.github.xjln.lang.Method;
import com.github.xjln.lang.ParameterList;
import com.github.xjln.lang.Variable;
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
                }
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
}
