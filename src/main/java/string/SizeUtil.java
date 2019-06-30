package string;

import resource.MessageApi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SizeUtil {
    static public long parseSize(String fieldName, String value, boolean isBinaryBased) throws Exception {
        Pattern pattern = Pattern.compile("^\\s*\\d+(\\.(\\d)*)?\\s*(k|m|g|t|K|M|G|T)?$\\s*");
        Matcher matcher = pattern.matcher(value);
        if (!matcher.find()) {
            // unable to parse
            throw MessageApi.getException("app00122",
                    "FIELD", fieldName,
                    "VALUE", value);
        }
        String group = matcher.group(0).trim();
        char lastChar = group.charAt(group.length() - 1);
        if (Character.isDigit(lastChar)) {
            return (long) Double.parseDouble(group);
        }
        String number = group.substring(0, group.length() - 1);
        double result = Double.parseDouble(number);
        if (lastChar != '.') {
            String unit = Character.toString(lastChar).toUpperCase();
            SizeUnit sizeUnit = SizeUnit.valueOf(unit);
            result *= sizeUnit.getMutiplier(isBinaryBased);
        }
        return (long) result;
    }
}
