package at.helpch.chatchat.util;

import at.helpch.chatchat.api.Format;
import at.helpch.chatchat.config.holders.FormatsHolder;
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

    private FormatUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static @NotNull Optional<ChatFormat> findPermissionFormat(
            @NotNull final Player player,
            @NotNull final Map<String, ChatFormat> formats) {
        return formats.values().stream()
                .filter(value -> player.hasPermission(FORMAT_PERMISSION + value.name()))
                .min(Comparator.comparingInt(ChatFormat::priority)); // lower number = higher priority
    }

    public static @NotNull ChatFormat findFormat(
            @NotNull final Player player,
            @NotNull final FormatsHolder formats) {
        final var formatOptional = findPermissionFormat(player, formats.formats());

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
            Placeholder.component("message", message)
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
