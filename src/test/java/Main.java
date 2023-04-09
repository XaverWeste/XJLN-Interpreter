import com.github.xjln.interpreter.Interpreter;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        var i = new Interpreter();
        i.execute(new File("src/test/java/Test.xjln"));
    }
}