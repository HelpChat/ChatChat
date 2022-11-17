package at.helpch.chatchat.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@DefaultQualifier(NonNull.class)
public final class PAPIMiniMessageProcessor {

    private static boolean containsLegacyColorCodes(final String string) {
        return string.indexOf(LegacyComponentSerializer.SECTION_CHAR) != -1;
    }

    public static Pair<String, TagResolver> process(final String input) {
        return process(null, input);
    }

    public static Pair<String, TagResolver> process(final @Nullable OfflinePlayer player, final String input) {
        return process(PlaceholderAPI.getPlaceholderPattern(), s -> PlaceholderAPI.setPlaceholders(player, s), input);
    }

    public static Pair<String, TagResolver> process(
        final Pattern pattern,
        final Function<String, String> placeholderResolver,
        final String input
    ) {
        final Matcher matcher = pattern.matcher(input);
        final List<TagResolver> placeholders = new ArrayList<>();
        final StringBuilder builder = new StringBuilder();
        int id = 0;

        while (matcher.find()) {
            final String match = matcher.group();
            final String replaced = placeholderResolver.apply(match);

            if (match.equals(replaced) || !containsLegacyColorCodes(replaced)) {
                matcher.appendReplacement(builder, Matcher.quoteReplacement(replaced));
            } else {
                final String key = "papi_generated_template_" + id;
                id++;
                placeholders.add(Placeholder.component(key, LegacyComponentSerializer.legacySection().deserialize(replaced)));
                matcher.appendReplacement(builder, Matcher.quoteReplacement("<" + key + ">"));
            }
        }

        matcher.appendTail(builder);

        return Pair.of(builder.toString(), TagResolver.resolver(placeholders));
    }

}
