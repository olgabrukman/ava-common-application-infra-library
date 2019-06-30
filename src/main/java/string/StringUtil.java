package string;

import java.io.LineNumberReader;
import java.io.StringReader;

public class StringUtil {

    @SuppressWarnings("all")
    static public boolean equalsIgnoreNull(String a, String b) {
        if ((a == null) && (b == null)) {
            return true;
        }
        if ((a == null) || (b == null)) {
            return false;
        }
        return a.equals(b);
    }

    @SuppressWarnings("all")
    static public boolean objectsEqual(Object a, Object b) {
        if ((a == null) && (b == null)) {
            return true;
        }
        if ((a == null) || (b == null)) {
            return false;
        }
        return a.toString().equals(b.toString());
    }

    static public boolean isEmpty(String string) {
        return string == null || (string.trim().length() == 0);
    }

    public static String setTrimmed(String value) {
        if (value == null) {
            return null;
        }

        return value.trim();
    }

    public static LineNumberReader getLineNumberReader(String value) throws Exception {
        StringReader stringReader = new StringReader(value);
        return new LineNumberReader(stringReader);
    }

    public static String breakLinesWidth(String lines, int width) throws Exception {
        if (lines == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        try (LineNumberReader reader = StringUtil.getLineNumberReader(lines)) {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                breakLine(width, result, line);
            }
        }
        return result.toString().trim();
    }

    private static void breakLine(int width, StringBuilder result, String line) {
        while (line.length() > width) {
            String left = line.substring(0, width);
            line = line.substring(width);
            if (result.length() > 0) {
                result.append("\n");
            }
            result.append(left.trim());
        }
        if (result.length() > 0) {
            result.append("\n");
        }
        result.append(line.trim());
    }

    public static String toStringSafe(Object object) {
        if (object == null) {
            return null;
        }
        return object.toString();
    }
}
