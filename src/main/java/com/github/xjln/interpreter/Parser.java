package com.github.xjln.interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

public class Parser {

    public final Scanner scanner;
    private Tokenhandler th;

    public Parser(){
        scanner = new Scanner();
    }

    public String parseFile(File file) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        java.util.Scanner sc = new java.util.Scanner(file);
        String line;

        while(sc.hasNextLine()){
            line = sc.nextLine().trim();
            if(line.equals("main")) sb.append(getContent(sc));
            else throw new RuntimeException("illegal argument in \"" + line +"\"");
        }

        return sb.toString();
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

        return sb.toString();
    }
}
