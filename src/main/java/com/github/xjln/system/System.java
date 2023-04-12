package com.github.xjln.system;

public class System {
    public static final Memory.SystemMemory MEM = new Memory.SystemMemory();

    public static String createName(String className){
        StringBuilder sb = new StringBuilder();
        sb.append("§").append(className).append("§");
        int i = 0;
        while(MEM.exist(sb.toString() + i)) i++;
        sb.append(i);
        return sb.toString();
    }
}
