package com.github.xjln.interpreter;

import com.github.xjln.lang.Class;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class Parser {

    public final Lexer lexer;
    private Scanner sc;
    private HashMap<String, String> uses;
    private Class current;
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
                    //parseDef(line);
                    //classes.put(className, current);
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
        //TODO
    }
}
