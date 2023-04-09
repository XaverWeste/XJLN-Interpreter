package com.github.xjln.interpreter;

import com.github.xjln.lang.ParameterList;

import java.io.File;
import java.io.FileNotFoundException;

public class Parser {

    public final Scanner scanner;

    public Parser(){
        scanner = new Scanner();
    }

    public AST createAST(Tokenhandler th){
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
            else if(line.startsWith("def")) parseMethodDef(sc, line);
            else throw new RuntimeException("illegal argument in \"" + line +"\"");
        }

        return sb.toString();
    }

    private void parseMethodDef(java.util.Scanner sc, String current){
        Tokenhandler th = new Tokenhandler(scanner.getTokens(current));
        th.assertToken("def");
        String name = th.assertToken(Token.Type.IDENTIFIER).s();
        th.assertToken("(");
        ParameterList pl = new ParameterList();
        Token t = th.next();
        while(!t.s().equals(")")){
            //TODO
        }
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
