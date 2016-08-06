package scalive;

public class Log {
    public static void log(String msg) {
        System.out.println("[Scalive] " + msg);
    }

    public static void logNoTag(String msg) {
        System.out.println(msg);
    }
}
