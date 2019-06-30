package validation;


import exception.AppException;
import org.apache.commons.validator.GenericValidator;

public class GeneralValidation {
    public static final String MUST_NOT_BE_EMPTY = " must not be empty";

    static public void validateNotEmpty(String fieldName, String value) throws Exception {
        if (GenericValidator.isBlankOrNull(value)) {
            throw new AppException(fieldName + MUST_NOT_BE_EMPTY);
        }
    }

    static public void validateEmpty(String fieldName, String value) throws Exception {
        if (!GenericValidator.isBlankOrNull(value)) {
            throw new AppException(fieldName + " must not be provided");
        }
    }

    static public void validateLength(String fieldName, String value, int maxLength) throws Exception {
        int length = value.length();
        if (length > maxLength) {
            throw new AppException(fieldName + " is too long. Max allowed length is " + maxLength + " characters");
        }
    }

    static public boolean isAlphanum(String value, String extraAllowedChars) throws Exception {
        String regex = "^[a-zA-Z0-9";
        if (extraAllowedChars != null) {
            regex += extraAllowedChars;
        }
        regex += "]*$";
        return value.matches(regex);
    }

    static public void validateAlphanum(String fieldName, String value, String extraAllowedChars) throws Exception {
        if (isAlphanum(value, extraAllowedChars)) {
            return;
        }

        String message = fieldName + " value " + value + " has invalid characters. Allowed characters are alphanumeric";
        if (extraAllowedChars != null) {
            message += " and '" + extraAllowedChars + "'";
        }
        throw new AppException(message);
    }

    public static void validateInteger(String fieldName, String value, boolean mandatory) throws Exception {
        if (mandatory) {
            validateNotEmpty(fieldName, value);
        }

        if (GenericValidator.isBlankOrNull(value)) {
            return;
        }

        if (!GenericValidator.isInt(value)) {
            throw new AppException(fieldName + "value '" + value + "' is invalid, must be an integer");
        }
    }

    public static void validateLong(String fieldName, String value, boolean mandatory) throws Exception {
        if (mandatory) {
            validateNotEmpty(fieldName, value);
        }

        if (GenericValidator.isBlankOrNull(value)) {
            return;
        }

        if (!GenericValidator.isLong(value)) {
            throw new AppException(fieldName + " must be long integer");
        }
    }

    public static void validateRange(String fieldName, int value, int min, int max) throws Exception {
        if ((value >= min) && (value <= max)) {
            return;
        }
        throw new AppException(fieldName + " must be in range " + min + " to " + max);
    }

    public static void validateAtLeast(String fieldName, int value, int min) throws Exception {
        if (value >= min) {
            return;
        }
        throw new AppException(fieldName + " must be at least " + min);
    }

    /*
    we block single quote because of karaf's gogo shell bug - unable to handle it.
    this causes the import configuration to fail
     */
    public static void blockSpecialCharacters(String fieldName, String value) {
        if (value == null) {
            return;
        }
        if (value.contains("'")) {
            throw new AppException(fieldName + " must not no contain character ' ");
        }
    }
}
