package at.helpch.chatchat.util;

public class StringUtils {
    public static boolean containsIllegalChars(String message) {
        for (char ch : message.toCharArray()) {
            if (ch <= 128 || ch == 167) {
                continue;
            }
            return true;
        }

        return false;
    }
}
