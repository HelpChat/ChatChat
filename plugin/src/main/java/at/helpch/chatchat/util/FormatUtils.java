package at.helpch.chatchat.util;

import at.helpch.chatchat.api.Format;
import at.helpch.chatchat.format.ChatFormat;
import at.helpch.chatchat.format.PMFormat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class FormatUtils {

    private static final String FORMAT_PERMISSION = "chatchat.format.";
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    private FormatUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static @NotNull ChatFormat createDefaultFormat() {
        var format = new ChatFormat();
        format.setPriority(1);
        format.setParts(List.of(
                "<gray>[</gray><color:#3dbbe4>Chat</color><color:#f3af4b>Chat</color><gray>]</gray> %player_name% » %message%"
        ));
        return format;
    }

    public static @NotNull PMFormat createDefaultPrivateMessageSenderFormat() {
        var format = new PMFormat();
        format.setParts(List.of(
                "<gray>you <yellow> » <gray>%recipient_player_name% <gray>:"
        ));
        return format;
    }

    public static @NotNull PMFormat createDefaultPrivateMessageReceiverFormat() {
        var format = new PMFormat();
        format.setParts(List.of(
                "<gray>%player_name% <yellow> » <gray>you <gray>:"
        ));
        return format;
    }

    public static @NotNull Optional<ChatFormat> findFormat(
            @NotNull final Player player,
            @NotNull final Map<String, ChatFormat> formats) {
        return formats.entrySet().stream()
                .filter(entry -> player.hasPermission(FORMAT_PERMISSION + entry.getKey()))
                .map(Map.Entry::getValue)
                .min(Comparator.comparingInt(ChatFormat::getPriority)); // lower number = higher priority
    }

    public static Component parseFormat(
            @NotNull final Format format,
            @NotNull final Player player,
            @NotNull final String message) {
        return format.getParts().stream()
                .map(part -> PlaceholderAPI.setPlaceholders(player, part))
                .map(part -> part.replace("%message%", message))
                .map(part -> part.replace("%recipient%", player.getName()))
                .map(FormatUtils::parseToMiniMessage)
                .collect(Component.toComponent());
    }

    public static @NotNull Component parseToMiniMessage(@NotNull final String formatPart) {
        return miniMessage.deserialize(formatPart, TagResolver.standard());
    }
}
