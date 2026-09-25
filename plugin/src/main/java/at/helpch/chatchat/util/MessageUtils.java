package at.helpch.chatchat.util;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import at.helpch.chatchat.placeholder.MiniPlaceholderContext;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class MessageUtils {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    private MessageUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static @NotNull Component parseToMiniMessage(@NotNull final String formatPart) {
        return miniMessage.deserialize(formatPart);
    }

    public static @NotNull Component parseToMiniMessage(@NotNull final String formatPart, @NotNull final TagResolver tag) {
        return miniMessage.deserialize(formatPart, tag);
    }

    public static @NotNull Component parseToMiniMessage(@NotNull final String formatPart, @NotNull final TagResolver... tags) {
        return miniMessage.deserialize(formatPart, tags);
    }

    public static @NotNull Component parseToMiniMessage(@NotNull final String formatPart, @NotNull final List<TagResolver> tags) {
        return miniMessage.deserialize(formatPart, TagResolver.resolver(tags));
    }

    public static @NotNull Component parseConfiguredMessage(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final User recipient,
        @NotNull final String template
    ) {
        final ChatUser chatUser = recipient instanceof ChatUser ? (ChatUser) recipient : null;
        final Player player = chatUser == null ? null : chatUser.player().orElse(null);
        final var text = FormatUtils.parsePlaceholders(template,
            placeholder -> {
                final var parsed = PlaceholderAPI.setPlaceholders(player, placeholder);
                return player == null ? parsed : PlaceholderAPI.setRelationalPlaceholders(player, player, parsed);
            });
        final var tags = TagResolver.builder()
            .resolver(PapiTagUtils.createPlaceholderAPITag(player))
            .resolver(plugin.miniPlaceholdersManager().compileTags(MiniPlaceholderContext.builder()
                .sender(chatUser).recipient(recipient).build()));

        if (player != null) {
            tags.resolver(PapiTagUtils.createRelPlaceholderAPITag(player, player));
            tags.resolver(PapiTagUtils.createRecipientTag(player));
            tags.resolver(ItemUtils.createItemPlaceholder(
                plugin.configManager().settings().itemFormat(),
                plugin.configManager().settings().itemFormatInfo(),
                player.getInventory().getItemInMainHand()));
        }

        return parseToMiniMessage(text, tags.build());
    }

}
