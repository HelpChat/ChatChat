package at.helpch.chatchat.util;

import at.helpch.chatchat.format.ChatFormat;
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
                "<click:open_url:\"https://google.com\"><hover:show_text:\"I am chatting in the %channel% channel<newline>Some new line\">%channel_prefix%</hover></click>",
                "<hover:show_text:\"Hey look, i am in the %vault_group% permission group.<newline>Some new line\"> [%vault_group%]</hover>",
                "<hover:show_text:\"Hey look, i am in the %vault_group% permission group.<newline>Some new line\"> %player_name%</hover>",
                "<hover:show_text:\"Cool diver tooltip here\"> ></hover>",
                "%message%",
                "<hover:show_text:\"This forces everyone to have a ! on the end. Haha\">!</hover>")
        );
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

    public static @NotNull Component parseToMiniMessage(@NotNull final String formatPart) {
        return miniMessage.deserialize(formatPart, TagResolver.standard());
    }
}
