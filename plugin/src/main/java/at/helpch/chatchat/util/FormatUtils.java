package at.helpch.chatchat.util;

import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.format.Format;
import at.helpch.chatchat.api.format.PriorityFormat;
import at.helpch.chatchat.api.holder.GlobalFormatsHolder;
import at.helpch.chatchat.format.ChatFormat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class FormatUtils {
    private static final String FORMAT_PERMISSION = "chatchat.format.";
    private static final String CHANNEL_FORMAT_PERMISSION = "chatchat.channel.format.";

    private FormatUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static @NotNull Optional<PriorityFormat> findPermissionFormat(
            @NotNull final Player player,
            @NotNull final Channel channel,
            @NotNull final Map<String, PriorityFormat> formats) {
        final var channelFormat = channel.formats().formats().values().stream()
            .filter(format -> player.hasPermission(CHANNEL_FORMAT_PERMISSION + channel.name() + "." + format.name()))
            .min(Comparator.comparingInt(PriorityFormat::priority)); // lower number = higher priority

        if (channelFormat.isPresent()) {
            // Channel formats take precedent.
            return channelFormat;
        }

        return formats.values().stream()
            .filter(value -> player.hasPermission(FORMAT_PERMISSION + value.name()))
            .min(Comparator.comparingInt(PriorityFormat::priority)); // lower number = higher priority
    }

    public static @NotNull PriorityFormat findFormat(
        @NotNull final Player player,
        @NotNull final Channel channel,
        @NotNull final GlobalFormatsHolder formats) {
        final var formatOptional = findPermissionFormat(player, channel, formats.formats());

        return formatOptional.orElse(ChatFormat.defaultFormat());
    }

    public static @NotNull Component parseConsoleFormat(
        @NotNull final Format format,
        @NotNull final Player player) {
        return MessageUtils.parseToMiniMessage(
            PlaceholderAPI.setPlaceholders(
                player,
                format.parts()
                    .values()
                    .stream()
                    .map(part -> String.join("", part))
                    .collect(Collectors.joining())
            ).replace("<message>", "%2$s")
        );
    }

    public static @NotNull Component parseFormat(
            @NotNull final Format format,
            @NotNull final Player player,
            @NotNull final ComponentLike message) {
        return MessageUtils.parseToMiniMessage(
            PlaceholderAPI.setPlaceholders(
                player,
                format.parts()
                    .values()
                    .stream()
                    .map(part -> String.join("", part))
                    .collect(Collectors.joining())
            ),
            Placeholder.component("message", message),
            PapiTagUtils.createPlaceholderAPITag(player)
        );
    }

    public static @NotNull Component parseFormat(
        @NotNull final Format format,
        @NotNull final Player player,
        @NotNull final Player recipient,
        @NotNull final ComponentLike message) {
        return MessageUtils.parseToMiniMessage(
            PlaceholderAPI.setRelationalPlaceholders(
                player,
                recipient,
                PlaceholderAPI.setPlaceholders(
                    player,
                    format.parts()
                        .values()
                        .stream()
                        .map(part -> String.join("", part))
                        .collect(Collectors.joining())
                )
            ),
            Placeholder.component("message", message),
            PapiTagUtils.createPlaceholderAPITag(player),
            PapiTagUtils.createRelPlaceholderAPITag(player, recipient),
            recipientTagResolver(recipient)
        );
    }

    private static @NotNull TagResolver recipientTagResolver(@NotNull final Player player) {
        return TagResolver.builder()
            // Could make the name a set of strings if we want more alternative namings.
            .tag("recipient", (queue, context) -> queue.hasNext()
                // Parse <recipient:PLACEHOLDERS>
                ? Tag.selfClosingInserting(
                    LegacyComponentSerializer.legacySection()
                        .deserialize(PlaceholderAPI.setPlaceholders(player, '%' + queue.pop().value() + '%')))

                // Parse <recipient>
                : Tag.selfClosingInserting(Component.text(player.getName()))
            ).build();
    }
}
