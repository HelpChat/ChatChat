package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import org.bukkit.Bukkit;
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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.usersHolder().getUser(event.getPlayer()));
    }

    @EventHandler
    private void onLeave(final PlayerQuitEvent event) {

        // find everyone who last messaged the person leaving, and remove their reference
        plugin.usersHolder().users().stream()
                .filter(user -> user instanceof ChatUser)
                .map(user -> (ChatUser) user)
                .filter(user -> user.lastMessagedUser().isPresent())
                .filter(user -> user.lastMessagedUser().get().player().equals(event.getPlayer()))
                .forEach(user -> user.lastMessagedUser(null));

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.usersHolder().removeUser(event.getPlayer().getUniqueId()));
    }
}
