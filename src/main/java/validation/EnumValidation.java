package validation;

import resource.MessageApi;
import string.StringUtil;

import java.util.Arrays;

public class EnumValidation {
    static public boolean validateBooleanAndGet(String fieldName, String value, boolean defaultValue)
            throws Exception {
        if (StringUtil.isEmpty(value)) {
            return defaultValue;
        }
        validateBoolean(fieldName, value);
        return Boolean.parseBoolean(value);
    }

    static public void validateBoolean(String fieldName, String value) throws Exception {
        String[] values = new String[]{"true", "false"};
        validateEnum(fieldName, values, value);
    }

    static public void validateEnum(String fieldName, Object[] allowedValues, String value) throws Exception {
        GeneralValidation.validateNotEmpty(fieldName, value);

        for (Object allowedValue : allowedValues) {
            if (allowedValue.toString().equals(value)) {
                return;
            }
        }

        // value must be one of ...
        throw MessageApi.getException("app00760",
                "VALUE", value,
                "FIELD", fieldName,
                "ALLOWED", Arrays.toString(allowedValues));
    }

    static public <E extends Enum<E>> E validateEnumAndGet(String fieldName, Class<E> enumClass, String value)
            throws Exception {
        validateEnum(fieldName, enumClass.getEnumConstants(), value);
        return Enum.valueOf(enumClass, value);
    }
}
