package database.tools;

import java.util.regex.Pattern;

public class StringManipulator {

    private static final Pattern numericPattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public static String toCapitalCase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static boolean isNotNumeric(String strNum) {
        if (strNum == null) {
            return true;
        }
        return !numericPattern.matcher(strNum).matches();
    }
}
