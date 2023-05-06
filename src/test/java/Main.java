import com.github.xjln.interpreter.XJLN;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println(XJLN.getInstance().execute(new File("src/test/java/Test.xjln")));
    }
}