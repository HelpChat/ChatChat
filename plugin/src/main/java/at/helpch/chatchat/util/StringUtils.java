package at.helpch.chatchat.util;

public class StringUtils {
    public static boolean containsIllegalChars(String message) {
        for (char ch : message.toCharArray()) {
            if (ch > 128 && ch < 167 || ch > 167) {
                return true;
            }
        }

        return false;
    }
}
