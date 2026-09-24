package at.helpch.chatchat.util;

import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.format.Format;
import at.helpch.chatchat.api.format.PriorityFormat;
import at.helpch.chatchat.api.holder.GlobalFormatsHolder;
import at.helpch.chatchat.format.ChatFormat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class FormatUtils {
    private static final String FORMAT_PERMISSION = "chatchat.format.";
    private static final String CHANNEL_FORMAT_PERMISSION = "chatchat.channel.format.";
    // Pass complete placeholders to PlaceholderAPI so a literal % cannot consume the next opening %.
    private static final Pattern PAPI_PLACEHOLDER = Pattern.compile("%[a-zA-Z0-9]+_[^%]+%");

    private FormatUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    private static @NotNull String parsePlaceholders(
        @NotNull final String text,
        @NotNull final Function<String, String> replacement
    ) {
        final var matcher = PAPI_PLACEHOLDER.matcher(text);
        final var parsed = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(parsed, Matcher.quoteReplacement(replacement.apply(matcher.group())));
        }

        matcher.appendTail(parsed);
        return parsed.toString();
    }

    public static @NotNull Optional<PriorityFormat> findPermissionFormat(
        @NotNull final Player player,
        @NotNull final Channel channel,
        @NotNull final Map<String, PriorityFormat> formats,
        final boolean inversePriorities) {
        // If priorities are inverted, lower number = higher priority.
        final var channelFormat = inversePriorities ?
            channel.formats().formats().values().stream()
                .filter(format -> player.hasPermission(CHANNEL_FORMAT_PERMISSION + channel.name() + "." + format.name()))
                .min(Comparator.comparingInt(PriorityFormat::priority)) :
            channel.formats().formats().values().stream()
                .filter(format -> player.hasPermission(CHANNEL_FORMAT_PERMISSION + channel.name() + "." + format.name()))
                .max(Comparator.comparingInt(PriorityFormat::priority)) ;

        // Channel formats have priority.
        if (channelFormat.isPresent()) {
            return channelFormat;
        }

        // If priorities are inverted, lower number = higher priority.
        return inversePriorities ?
            formats.values().stream()
                .filter(value -> player.hasPermission(FORMAT_PERMISSION + value.name()))
                .min(Comparator.comparingInt(PriorityFormat::priority)) :
            formats.values().stream()
                .filter(value -> player.hasPermission(FORMAT_PERMISSION + value.name()))
                .max(Comparator.comparingInt(PriorityFormat::priority));
    }

    public static @NotNull PriorityFormat findFormat(
        @NotNull final Player player,
        @NotNull final Channel channel,
        @NotNull final GlobalFormatsHolder formats,
        final boolean inversePriorities) {
        final var formatOptional = findPermissionFormat(player, channel, formats.formats(), inversePriorities);

        return formatOptional.orElse(ChatFormat.defaultFormat());
    }

    public static @NotNull Component parseConsoleFormat(
        @NotNull final Format format,
        @NotNull final Player player) {
        return MessageUtils.parseToMiniMessage(
            parsePlaceholders(
                format.parts()
                    .values()
                    .stream()
                    .map(part -> String.join("", part))
                    .collect(Collectors.joining()),
                placeholder -> PlaceholderAPI.setPlaceholders(player, placeholder)
            ).replace("<message>", "%2$s")
        );
    }

    public static @NotNull Component parseFormat(
        @NotNull final Format format,
        @NotNull final Player player,
        @NotNull final ComponentLike message) {
        return parseFormat(format, player, message, TagResolver.empty());
    }

    public static @NotNull Component parseFormat(
        @NotNull final Format format,
        @NotNull final Player player,
        @NotNull final ComponentLike message,
        @NotNull final TagResolver miniPlaceholders) {
        return MessageUtils.parseToMiniMessage(
            parsePlaceholders(
                format.parts()
                    .values()
                    .stream()
                    .map(part -> String.join("", part))
                    .collect(Collectors.joining()),
                placeholder -> PlaceholderAPI.setPlaceholders(player, placeholder)
            ),
            Placeholder.component("message", message),
            PapiTagUtils.createPlaceholderAPITag(player),
            miniPlaceholders
        );
    }

    public static @NotNull Component parseFormat(
        @NotNull final Format format,
        @NotNull final ComponentLike message) {
        return parseFormat(format, message, TagResolver.empty());
    }

    public static @NotNull Component parseFormat(
        @NotNull final Format format,
        @NotNull final ComponentLike message,
        @NotNull final TagResolver miniPlaceholders) {
        return MessageUtils.parseToMiniMessage(
            parsePlaceholders(
                format.parts()
                    .values()
                    .stream()
                    .map(part -> String.join("", part))
                    .collect(Collectors.joining()),
                placeholder -> PlaceholderAPI.setPlaceholders(null, placeholder)
            ),
            Placeholder.component("message", message),
            PapiTagUtils.createPlaceholderAPITag(null),
            miniPlaceholders
        );
    }

    public static @NotNull Component parseFormat(
        @NotNull final Format format,
        @NotNull final Player player,
        @NotNull final Player recipient,
        @NotNull final ComponentLike message) {
        return parseFormat(format, player, recipient, message, TagResolver.empty());
    }

    public static @NotNull Component parseFormat(
        @NotNull final Format format,
        @NotNull final Player player,
        @NotNull final Player recipient,
        @NotNull final ComponentLike message,
        @NotNull final TagResolver miniPlaceholders) {
        return MessageUtils.parseToMiniMessage(
            parsePlaceholders(
                format.parts()
                    .values()
                    .stream()
                    .map(part -> String.join("", part))
                    .collect(Collectors.joining()),
                placeholder -> PlaceholderAPI.setRelationalPlaceholders(
                    player,
                    recipient,
                    PlaceholderAPI.setPlaceholders(player, placeholder)
                )
            ),
            Placeholder.component("message", message),
            PapiTagUtils.createPlaceholderAPITag(player),
            PapiTagUtils.createRelPlaceholderAPITag(player, recipient),
            PapiTagUtils.createRecipientTag(recipient),
            miniPlaceholders
        );
    }
}
