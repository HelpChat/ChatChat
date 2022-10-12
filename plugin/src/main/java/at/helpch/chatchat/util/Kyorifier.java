package at.helpch.chatchat.util;

import com.google.common.collect.ImmutableMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public final class Kyorifier {
    private static final ImmutableMap<Character, String> COLOURS = new ImmutableMap.Builder<Character, String>()
        .put('0', NamedTextColor.BLACK.toString())
        .put('1', NamedTextColor.DARK_BLUE.toString())
        .put('2', NamedTextColor.DARK_GREEN.toString())
        .put('3', NamedTextColor.DARK_AQUA.toString())
        .put('4', NamedTextColor.DARK_RED.toString())
        .put('5', NamedTextColor.DARK_PURPLE.toString())
        .put('6', NamedTextColor.GOLD.toString())
        .put('7', NamedTextColor.GRAY.toString())
        .put('8', NamedTextColor.DARK_GRAY.toString())
        .put('9', NamedTextColor.BLUE.toString())
        .put('a', NamedTextColor.GREEN.toString())
        .put('b', NamedTextColor.AQUA.toString())
        .put('c', NamedTextColor.RED.toString())
        .put('d', NamedTextColor.LIGHT_PURPLE.toString())
        .put('e', NamedTextColor.YELLOW.toString())
        .put('f', NamedTextColor.WHITE.toString())
        .build();

    private static final ImmutableMap<Character, String> FORMATTERS = new ImmutableMap.Builder<Character, String>()
        .put('k', TextDecoration.OBFUSCATED.toString())
        .put('l', TextDecoration.BOLD.toString())
        .put('m', TextDecoration.STRIKETHROUGH.toString())
        .put('n', TextDecoration.UNDERLINED.toString())
        .put('o', TextDecoration.ITALIC.toString())
        // There is no way to get the reset tag name from Adventure. Also doesn't really matter as we never actually
        // use it. Instead, we close all the opened tags.
        .put('r', "reset")
        .build();

    private static final Pattern LEGACY_HEX_COLORS_PATTERN = Pattern.compile(
        "&(?<code>[\\da-fk-or])|[&{\\[<]?[#x](?<hex>(&?[a-f\\d]){6})[}\\]>]?",
        Pattern.CASE_INSENSITIVE // Turns out colors are not case-sensitive.
    );

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
            final var colour = hex == null
                ? COLOURS.get(Character.toLowerCase(code.charAt(0)))
                : "#" + hex.replace("&", "");

            if (colour == null) {
                final var formatter = FORMATTERS.get(Character.toLowerCase(code.charAt(0)));
                if (formatter.equals("reset")) {
                    return closeAll(activeFormatters).toString();
                }
                activeFormatters.push(formatter);
                return "<" + formatter + ">";
            }

            final var out = closeAll(activeFormatters);
            out.append("<").append(colour).append(">");
            return out.toString();
        });
    }
}
