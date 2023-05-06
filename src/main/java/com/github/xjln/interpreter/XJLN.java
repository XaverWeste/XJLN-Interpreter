package com.github.xjln.interpreter;

import java.io.File;

public class XJLN {

    private static XJLN INSTANCE = null;

    private final Interpreter interpreter;

    private XJLN(){
        interpreter = new Interpreter();
    }

    /**
     * Returns the Instance representing XJLN.
     * If there's no instance it will be created a new one.
     */
    public static XJLN getInstance(){
        if(INSTANCE == null) INSTANCE = new XJLN();
        return INSTANCE;
    }

    /**
     * Executes the given .xjln file and returns the output.
     */
    public String execute(File file){
        return interpreter.execute(file, "");
    }
}