package com.github.xjln.interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

class Interpreter {

    private final Parser parser;
    private ArrayList<String> output;

    public Interpreter(){
        parser = new Parser();
        output = new ArrayList<>();
        output.add("\n");
        //parseStandardUse();
    }

    private void parseStandardUse(){
        String[] files = new String[]{
                parser.LIBPATH + "lib/System.xjln",
                parser.LIBPATH + "lib/lang/Bool.xjln"
        };

        for(String path:files) parser.parseFile(path);
    }

    public String execute(File file, String code){
        if(file != null){
            try{
                execute(file);
            }catch (RuntimeException e){
                output.add("Error: " + e.getMessage());
            }
        }else{
            output.add("Error: Not supported yet");
        }
        StringBuilder sb = new StringBuilder();
        for(String s:output) sb.append(s).append("\n");
        return sb.toString();
    }

    private void execute(File file){
        if(!file.getName().endsWith(".xjln")) throw new RuntimeException("expected .xjln file");
        if(!file.exists()) throw new RuntimeException("file with path: " + file.getPath() + " does not exist");

        try {
            parser.parseFile(file);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        output.add("\nXJLN Process finished successfully\n");
    }

}
