package at.helpch.chatchat.util;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class PAPIMiniMessageProcessor {

    public static String process(final String input) {
        return process(null, input);
    }

    public static String process(final @Nullable OfflinePlayer player, final String input) {
        return process(PlaceholderAPI.getPlaceholderPattern(), s -> PlaceholderAPI.setPlaceholders(player, s), input);
    }

    public static String process(
        final Pattern pattern,
        final Function<String, String> placeholderResolver,
        final String input
    ) {
        return pattern.matcher(input).replaceAll(matchResult ->
            Matcher.quoteReplacement(
                Kyorifier.kyorify(
                    placeholderResolver.apply(matchResult.group()))));
    }

}
