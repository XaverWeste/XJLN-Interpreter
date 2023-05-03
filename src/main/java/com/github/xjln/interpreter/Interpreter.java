package com.github.xjln.interpreter;

import java.io.File;

public class Interpreter {

    private final Parser parser;

    public Interpreter(){
        parser = new Parser();
    }

    public void execute(File file){
        if(!file.exists()) throw new RuntimeException("file with path: " + file.getPath() + " does not exist");

        java.lang.System.out.println("\nXJLN Process finished successfully\n");
    }

}
