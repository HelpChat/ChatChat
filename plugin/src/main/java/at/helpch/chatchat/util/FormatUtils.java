package at.helpch.chatchat.util;

import at.helpch.chatchat.api.Format;
import at.helpch.chatchat.config.FormatsHolder;
import at.helpch.chatchat.format.ChatFormat;
import java.util.List;
import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public final class FormatUtils {

    private static final Pattern DEFAULT_URL_PATTERN = Pattern.compile("(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?");
    private static final String URL_PERMISSION = "chatchat.url";
    private static final TextReplacementConfig URL_REPLACER = TextReplacementConfig.builder()
        .match(FormatUtils.DEFAULT_URL_PATTERN)
        .replacement(builder -> builder.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, builder.content())))
        .build();

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
            @NotNull final Player player,
            @NotNull final ComponentLike message) {
        return format.parts().stream()
            .map(part -> PlaceholderAPI.setPlaceholders(player, part))
            .map(part -> FormatUtils.parseToMiniMessage(part,
                Placeholder.component("message", !player.hasPermission(URL_PERMISSION)
                    ? message
                    : message.asComponent().replaceText(URL_REPLACER))))
            .collect(Component.toComponent());
    }

    public static @NotNull Component parseFormat(
        @NotNull final Format format,
        @NotNull final Player player,
        @NotNull final Player recipient,
        @NotNull final ComponentLike message) {
        return format.parts().stream()
            .map(part -> PlaceholderAPI.setPlaceholders(player, part))
            .map(part -> replaceRecipientPlaceholder(recipient, part))
            .map(part -> FormatUtils.parseToMiniMessage(part,
                Placeholder.component("message", !player.hasPermission(URL_PERMISSION)
                    ? message
                    : message.asComponent().replaceText(URL_REPLACER))))
            .collect(Component.toComponent());
    }

    public static @NotNull Component parseToMiniMessage(@NotNull final String formatPart) {
        return miniMessage.deserialize(formatPart);
    }

    public static @NotNull Component parseToMiniMessage(@NotNull final String formatPart, @NotNull final TagResolver tag) {
        return miniMessage.deserialize(formatPart, tag);
    }

    public static @NotNull Component parseToMiniMessage(@NotNull final String formatPart, @NotNull final List<TagResolver> tags) {
        return miniMessage.deserialize(formatPart, TagResolver.resolver(tags));
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
