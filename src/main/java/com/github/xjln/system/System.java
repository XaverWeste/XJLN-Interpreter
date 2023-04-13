package com.github.xjln.system;

import com.github.xjln.lang.NativeMethod;
import com.github.xjln.lang.ParameterList;
import org.jetbrains.annotations.NotNull;

public class System {
    public static final Memory.SystemMemory MEM = new Memory.SystemMemory();

    @NotNull
    public static String createName(String className){
        StringBuilder sb = new StringBuilder();
        sb.append("§").append(className).append("§");
        int i = 0;
        while(MEM.exist(sb.toString() + i)) i++;
        sb.append(i);
        return sb.toString();
    }

    public static NativeMethod getNativeMethod(String className, String methodName, ParameterList pl){
        return switch (className){
            case "$" -> switch (methodName){
                case "log" -> new NativeMethod(pl, (o, mem) -> java.lang.System.out.println(mem.vars.get("s").value()));
                default -> throw new RuntimeException();
            };
            default -> throw new RuntimeException("for class " + className + " is no native method " + methodName + "() defined");
        };
    }
}