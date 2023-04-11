package com.github.xjln.system;

public class System {
    public static final Memory.SystemMemory mem=new Memory.SystemMemory();

    public static String createName(String className){
        StringBuilder sb = new StringBuilder();
        sb.append("§").append(className);
        int i = 0;
        while(mem.exist(sb.toString() + i)) i++;
        sb.append(i);
        return sb.toString();
    }
}
