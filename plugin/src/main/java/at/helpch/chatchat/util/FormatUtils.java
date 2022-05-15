package at.helpch.chatchat.util;

import at.helpch.chatchat.api.Format;
import at.helpch.chatchat.config.holders.FormatsHolder;
import at.helpch.chatchat.format.ChatFormat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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
            replaceRecipientPlaceholder(
                recipient,
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
                )
            ),
            Placeholder.component("message", message)
        );
    }

    private static @NotNull String replaceRecipientPlaceholder(@NotNull final Player player, @NotNull final String toReplace) {

        if (!toReplace.contains("%recipient")) {
            return toReplace;
        }

        return PlaceholderAPI.setPlaceholders(
            player,
            toReplace
                .replace("%recipient%", player.getName())
                // This is to support PAPI placeholders for the recipient. Ex: %recipient_player_name%.
                // TODO: Improve this. We need an actual parser for this instead of this. Possibly an even better idea
                //  is to use MiniMessage tags instead.
                .replace("%recipient_", "%")
            );
    }
}
