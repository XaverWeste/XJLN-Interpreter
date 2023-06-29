package com.github.xjln.interpreter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

class Interpreter {

    private final Parser parser;
    private ArrayList<String> output;

    public Interpreter(){
        parser = new Parser();
        output = new ArrayList<>();
        output.add("\n");
    }

    public String execute(String src){
        if(src != null){
            if(!Files.exists(Paths.get(src))) output.add("unable to find source folder");
            else{
                /*
                for (File fileEntry : Objects.requireNonNull(folder.listFiles())){
                    if(fileEntry.isDirectory()) compileFolder(fileEntry);
                    else if(fileEntry.getName().endsWith(".xjln")) classes.putAll(parser.parseFile(fileEntry));
                }

                 */
                output.add("\nXJLN Process finished successfully\n");
            }
        }else output.add("src folder is null");

        StringBuilder sb = new StringBuilder();
        for(String s:output) sb.append(s).append("\n");
        return sb.toString();
    }

}
