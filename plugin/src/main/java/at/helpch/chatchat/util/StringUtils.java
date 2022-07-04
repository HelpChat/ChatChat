package at.helpch.chatchat.util;

public final class StringUtils {
    private StringUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static boolean containsIllegalChars(String message) {
        for (char ch : message.toCharArray()) {
            if (ch <= 127 || ch == 167) {
                continue;
            }
            return true;
        }

        return false;
    }
}
