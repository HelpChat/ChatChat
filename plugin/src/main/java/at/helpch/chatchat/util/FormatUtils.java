package at.helpch.chatchat.util;

import at.helpch.chatchat.api.Format;
import at.helpch.chatchat.config.holders.FormatsHolder;
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

    private static final Pattern DEFAULT_URL_PATTERN = Pattern.compile("(?:(https?)://)?([-\\w_.]+\\.\\w{2,})(/\\S*)?");
    private static final Pattern URL_SCHEME_PATTERN = Pattern.compile("^[a-z][a-z0-9+\\-.]*:");

    private static final TextReplacementConfig URL_REPLACER_CONFIG = TextReplacementConfig.builder()
        .match(DEFAULT_URL_PATTERN)
        .replacement(builder -> {
            String clickUrl = builder.content();
            if (!URL_SCHEME_PATTERN.matcher(clickUrl).find()) {
                clickUrl = "https://" + clickUrl;
            }
            return builder.clickEvent(ClickEvent.openUrl(clickUrl));
        })
        .build();

    private static final String URL_PERMISSION = "chatchat.url";
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
            .map(part -> MessageUtils.parseToMiniMessage(part,
                Placeholder.component("message", !player.hasPermission(URL_PERMISSION)
                    ? message
                    : message.asComponent().replaceText(URL_REPLACER_CONFIG))))
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
            .map(part -> MessageUtils.parseToMiniMessage(part,
                Placeholder.component("message", !player.hasPermission(URL_PERMISSION)
                    ? message
                    : message.asComponent().replaceText(URL_REPLACER_CONFIG))))
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
