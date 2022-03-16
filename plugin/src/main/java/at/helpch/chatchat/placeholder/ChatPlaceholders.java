package at.helpch.chatchat.placeholder;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.util.ChannelUtils;
import java.util.List;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
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
            "%chatchat_channel_prefix%"
        );
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(final OfflinePlayer offlinePlayer, @NotNull final String input) {
        final var params = input.split("_");

        if (offlinePlayer == null) {
            return "";
        }

        if (!offlinePlayer.isOnline()) {
            return "";
        }

        final var player = offlinePlayer.getPlayer();

        if (player == null) {
            return "";
        }

        final var user = plugin.usersHolder().getUser(player);

        if (params.length < 2) {
            return null;
        }

        if (params[0].equals("channel")) {
            switch (params[1]) {
                case "name":
                    return user.channel().name();
                case "prefix":
                    return user.channel().channelPrefix();
            }

        }

        return null;
    }
}
