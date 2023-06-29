package com.github.xjln.system;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class System {
    public static final Memory.SystemMemory MEM = new Memory.SystemMemory();;

    @NotNull
    public static String createName(String className){
        StringBuilder sb = new StringBuilder();
        sb.append("§").append(className).append("§");
        int i = 0;
        while(MEM.existO(sb.toString() + i)) i++;
        sb.append(i);
        return sb.toString();
    }

    public static boolean validateName(String className){
        if(className.matches("[a-zA-Z]+")) return true;
        return Set.of("§", "$").contains(className);
    }
}