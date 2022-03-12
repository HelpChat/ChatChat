package at.helpch.chatchat.util;

import at.helpch.chatchat.api.Format;
import at.helpch.chatchat.api.User;
import at.helpch.chatchat.config.FormatsHolder;
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

    private static final int RECIPIENT_SUBSTRING = 11; // %recipient_
    private static final String FORMAT_PERMISSION = "chatchat.format.";
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    private FormatUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static @NotNull Optional<ChatFormat> findPermissionFormat(
            @NotNull final Player player,
            @NotNull final Map<String, ChatFormat> formats) {
        return formats.entrySet().stream()
                .filter(entry -> player.hasPermission(FORMAT_PERMISSION + entry.getKey()))
                .map(Map.Entry::getValue)
                .min(Comparator.comparingInt(ChatFormat::priority)); // lower number = higher priority
    }

    public static @NotNull ChatFormat findFormat(
            @NotNull final Player player,
            @NotNull final FormatsHolder formats) {
        final var formatOptional = findPermissionFormat(player, formats.formats());
        final var defaultFormat = formats.formats().getOrDefault(formats.defaultFormat(), ChatFormat.DEFAULT_FORMAT);

        return formatOptional.orElse(defaultFormat);
    }

    public static @NotNull Component parseFormat(
            @NotNull final Format format,
            @NotNull final User user,
            @NotNull final String message) {
        return format.parts().stream()
                .map(part -> PlaceholderAPI.setPlaceholders(user.player(), part))
                .map(part -> part.replace("%message%", message))
                .map(part -> part.replace("%channel_prefix%", user.channel().channelPrefix()))
                .map(part -> replaceRecipientPlaceholder(user.player(), part))
                .map(FormatUtils::parseToMiniMessage)
                .collect(Component.toComponent());
    }

    public static @NotNull Component parseToMiniMessage(@NotNull final String formatPart) {
        return miniMessage.deserialize(formatPart, TagResolver.standard());
    }

    private static @NotNull String replaceRecipientPlaceholder(@NotNull final Player player, @NotNull final String toReplace) {

        // only replace if it actually contains a recipient placeholder
        if (!toReplace.contains("%recipient")) {
            return toReplace;
        }

        if (toReplace.equalsIgnoreCase("%recipient%")) {
            return player.getName();
        }

        if (toReplace.length() <= RECIPIENT_SUBSTRING) {
            return toReplace; // prevents IndexOutOfBoundsException from String#substring
        }

        //set any PAPI placeholders after %recipient_
        return PlaceholderAPI.setPlaceholders(player, "%" + toReplace.substring(RECIPIENT_SUBSTRING));
    }
}
