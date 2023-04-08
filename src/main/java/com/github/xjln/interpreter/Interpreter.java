package com.github.xjln.interpreter;

import java.io.File;
import java.io.FileNotFoundException;

public class Interpreter {

    private final Parser parser;

    public Interpreter(){
        parser = new Parser();
    }

    public void execute(File file){
        if(!file.exists()) throw new RuntimeException("file with path: " + file.getPath() + " does not exist");

        try{
            for(String line:parser.parseFile(file).split("\n")){
                try{
                    execute(line);
                }catch (RuntimeException e){
                    throw new RuntimeException(e.getMessage() + ", in: " + line);
                }
            }
        }catch (FileNotFoundException ignored){} //won't throw
    }

    private void execute(String line){
        line = line.trim();
        if(!(line.equals("") || line.startsWith("#"))){

        }
    }
}
