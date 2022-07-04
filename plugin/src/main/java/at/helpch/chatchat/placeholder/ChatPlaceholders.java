package at.helpch.chatchat.placeholder;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import java.util.List;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.identity.Identity;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public final class ChatPlaceholders extends PlaceholderExpansion {
    private final ChatChatPlugin plugin;

    public ChatPlaceholders(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "chatchat";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return List.of(
            "%chatchat_channel_name%",
            "%chatchat_channel_prefix%",
            "%chatchat_channel_message_prefix%",
            "%chatchat_social_spy_enabled%",
            "%chatchat_private_messages_enabled%",
            "%chatchat_private_messages_recipient%"
        );
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(final OfflinePlayer offlinePlayer, @NotNull final String input) {
        final var parsedInput = PlaceholderAPI.setBracketPlaceholders(offlinePlayer, input);

        if (offlinePlayer != null && !offlinePlayer.isOnline()) {
            return null;
        }

        final var user = plugin.usersHolder().getUser(
            offlinePlayer == null
                ? Identity.nil().uuid()
                : offlinePlayer.getUniqueId()
        );

        switch(parsedInput) {
            case "channel_name":
                return user.channel().name();
            case "channel_prefix":
                return user.channel().channelPrefix();
            case "channel_message_prefix":
                return user.channel().messagePrefix();
            case "social_spy_enabled":
                return formatBoolean(plugin.usersHolder().isSocialSpy(user.uuid()));
        }

        if (!(user instanceof ChatUser)) {
            return null;
        }

        final var chatUser = (ChatUser) user;

        switch(parsedInput) {
            case "private_messages_enabled":
                return formatBoolean(chatUser.privateMessages());
            case "private_messages_recipient":
                return chatUser.lastMessagedUser().map(value -> value.player().getName()).orElse("");
        }

        return null;
    }


    private @NotNull String formatBoolean(boolean bool) {
        return bool ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
    }
}
