package com.github.xjln;

import com.github.xjln.interpreter.Interpreter;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        var i = new Interpreter();
        i.execute(new File("src/main/java/com/github/xjln/Test.xjln"));
    }
}