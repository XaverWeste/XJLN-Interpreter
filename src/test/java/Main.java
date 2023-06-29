import com.github.xjln.interpreter.XJLN;

public class Main {
    public static void main(String[] args) {
        XJLN.getInstance().execute("src/test/java", System.out::println);
    }
}