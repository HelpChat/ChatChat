package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.user.UsersHolder;
import at.helpch.chatchat.util.ChannelUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerListener implements Listener {

    private final ChatChatPlugin plugin;

    public PlayerListener(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onJoin(final PlayerJoinEvent event) {
        final var user = plugin.usersHolder().addUser(event.getPlayer());
        final var channelsConfig = plugin.configManager().channels();

        final var defaultChannel = ChannelUtils.findDefaultChannel(channelsConfig.channels(), channelsConfig.defaultChannel());
        user.channel(defaultChannel); // set them to the default channel as they join the server
    }

    @EventHandler
    private void onLeave(final PlayerQuitEvent event) {
        plugin.usersHolder().removeUser(event.getPlayer());
    }
}
