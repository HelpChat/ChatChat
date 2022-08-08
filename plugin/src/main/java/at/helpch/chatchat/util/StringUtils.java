package at.helpch.chatchat.util;

public final class StringUtils {

    private StringUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    // https://www.rapidtables.com/code/text/ascii-table.html
    // https://theasciicode.com.ar/extended-ascii-code/degree-symbol-ascii-code-248.html
    public static boolean containsIllegalChars(String message) {
        return message.chars().anyMatch(it -> it > 127 && it != 248);
    }

}
