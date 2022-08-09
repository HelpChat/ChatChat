package at.helpch.chatchat.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public final class Kyorifier {
    private static final Map<Character, String> COLOURS = new HashMap<>(15);
    private static final Map<Character, String> FORMATTERS = new HashMap<>(6);
    private static final Pattern LEGACY_HEX_COLORS_PATTERN = Pattern.compile(
        "&(?<code>[\\da-fk-or])|[&{\\[<]?[#x](?<hex>(&?[A-Fa-f\\d]){6})[}\\]>]?");

    static {
        COLOURS.put('0', NamedTextColor.BLACK.toString());
        COLOURS.put('1', NamedTextColor.DARK_BLUE.toString());
        COLOURS.put('2', NamedTextColor.DARK_GREEN.toString());
        COLOURS.put('3', NamedTextColor.DARK_AQUA.toString());
        COLOURS.put('4', NamedTextColor.DARK_RED.toString());
        COLOURS.put('5', NamedTextColor.DARK_PURPLE.toString());
        COLOURS.put('6', NamedTextColor.GOLD.toString());
        COLOURS.put('7', NamedTextColor.GRAY.toString());
        COLOURS.put('8', NamedTextColor.DARK_GRAY.toString());
        COLOURS.put('9', NamedTextColor.BLUE.toString());
        COLOURS.put('a', NamedTextColor.GREEN.toString());
        COLOURS.put('b', NamedTextColor.AQUA.toString());
        COLOURS.put('c', NamedTextColor.RED.toString());
        COLOURS.put('d', NamedTextColor.LIGHT_PURPLE.toString());
        COLOURS.put('e', NamedTextColor.YELLOW.toString());
        COLOURS.put('f', NamedTextColor.WHITE.toString());

        FORMATTERS.put('k', TextDecoration.OBFUSCATED.toString());
        FORMATTERS.put('l', TextDecoration.BOLD.toString());
        FORMATTERS.put('m', TextDecoration.STRIKETHROUGH.toString());
        FORMATTERS.put('n', TextDecoration.UNDERLINED.toString());
        FORMATTERS.put('o', TextDecoration.ITALIC.toString());
        // The <reset> tag is never placed. Instead, we close existent tags. So it doesn't matter what name we give
        // here. Also, there isn't a way to get the name of the reset tag from adventure.
        FORMATTERS.put('r', "reset");
    }

    private static StringBuilder closeAll(Stack<String> activeFormatters) {
        final var out = new StringBuilder();
        while (!activeFormatters.isEmpty()) {
            out.append("</").append(activeFormatters.pop()).append(">");
        }
        return out;
    }

    public static String kyorify(String input) {
        final Stack<String> activeFormatters = new Stack<>();
        return LEGACY_HEX_COLORS_PATTERN.matcher(input.replace("§", "&")).replaceAll(result -> {
            final Matcher matcher = (Matcher) result;
            final var hex = matcher.group("hex");
            final var code = matcher.group("code");
            final var colour = hex == null ? COLOURS.get(code.charAt(0)) : "#" + hex.replace("&", "");

            if (colour == null) {
                final var formatter = FORMATTERS.get(code.charAt(0));
                if (formatter.equals("reset")) {
                    return closeAll(activeFormatters).toString();
                }
                activeFormatters.push(formatter);
                return "<" + formatter + ">";
            } else {
                final var out = closeAll(activeFormatters);
                out.append("<").append(colour).append(">");

                return out.toString();
            }
        });
    }
}
