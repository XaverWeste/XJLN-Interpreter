package com.github.xjln.interpreter;

import java.io.File;
import java.io.FileNotFoundException;

public class Interpreter {

    private final Parser parser;

    public Interpreter(){
        parser = new Parser();
        //parseStandardUse();
    }

    private void parseStandardUse(){
        String[] files = new String[]{
                parser.LIBPATH + "lib/System.xjln",
                parser.LIBPATH + "lib/lang/Bool.xjln"
        };

        for(String path:files) parser.parseFile(path);
    }

    public void execute(File file){
        if(!file.exists()) throw new RuntimeException("file with path: " + file.getPath() + " does not exist");

        try {
            parser.parseFile(file);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        java.lang.System.out.println("\nXJLN Process finished successfully\n");
    }

}
