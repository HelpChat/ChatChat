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
        return format.parts().stream()
            .map(part -> PlaceholderAPI.setPlaceholders(player, part))
            .map(part -> MessageUtils.parseToMiniMessage(part, Placeholder.component("message", message)))
            .collect(Component.toComponent());
    }

    public static @NotNull Component parseFormat(
        @NotNull final Format format,
        @NotNull final Player player,
        @NotNull final Player recipient,
        @NotNull final ComponentLike message) {
        return format.parts().stream()
            .map(part -> PlaceholderAPI.setPlaceholders(player, part))
            .map(part -> PlaceholderAPI.setRelationalPlaceholders(player, recipient, part))
            .map(part -> replaceRecipientPlaceholder(recipient, part))
            .map(part -> MessageUtils.parseToMiniMessage(part, Placeholder.component("message", message)))
            .collect(Component.toComponent());
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
                // I know it can be better and probably needs a complex parser but that requires, time, skills and patience,
                // none of which I actually have.
                .replace("%recipient_", "%")
            );
    }
}
