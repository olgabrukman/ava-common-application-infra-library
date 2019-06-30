package test;

public class TestUtil {
    static public final long DEFAULT_TIMEOUT = 300000;

    static private String getCallerContextName() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        return stackTraceElements[3].getClassName() + "." + stackTraceElements[3].getMethodName();
    }

    static public void printTestStart() {
        System.out.println("Starting a test: " + getCallerContextName());
    }

    static public void printTestEnd() {
        System.out.println("Test " + getCallerContextName() + " finished successfully.");
    }
}
