package at.helpch.chatchat.util;

import at.helpch.chatchat.api.Format;
import at.helpch.chatchat.format.ChatFormat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public final class FormatUtils {

    private static final String FORMAT_PERMISSION = "chatchat.format.";
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    private FormatUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static @NotNull Optional<ChatFormat> findFormat(
            @NotNull final Player player,
            @NotNull final Map<String, ChatFormat> formats) {
        return formats.entrySet().stream()
                .filter(entry -> player.hasPermission(FORMAT_PERMISSION + entry.getKey()))
                .map(Map.Entry::getValue)
                .min(Comparator.comparingInt(ChatFormat::priority)); // lower number = higher priority
    }

    public static @NotNull Component parseFormat(
            @NotNull final Format format,
            @NotNull final Player player,
            @NotNull final String message) {
        return format.parts().stream()
                .map(part -> PlaceholderAPI.setPlaceholders(player, part))
                .map(part -> part.replace("%message%", message))
                .map(FormatUtils::parseToMiniMessage)
                .collect(Component.toComponent());
    }

    public static @NotNull Component parseFormat(
        @NotNull final Format format,
        @NotNull final Player player,
        @NotNull final Player receiver,
        @NotNull final String message) {
        return format.parts().stream()
            .map(part -> PlaceholderAPI.setPlaceholders(player, part))
            .map(part -> part.replace("%message%", message))
            .map(part -> replaceRecipientPlaceholder(receiver, part))
            .map(FormatUtils::parseToMiniMessage)
            .collect(Component.toComponent());
    }

    public static @NotNull Component parseToMiniMessage(@NotNull final String formatPart) {
        return miniMessage.deserialize(formatPart, TagResolver.standard());
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
