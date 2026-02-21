package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.LoginEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerListener implements Listener {

    private static final String JOIN_MESSAGE_PERMISSION = "chatchat.joinmessage";
    private static final String LEAVE_MESSAGE_PERMISSION = "chatchat.leavemessage";

    private final ChatChatPlugin plugin;

    public PlayerListener(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.usersHolder().getUser(player));
    }

    @EventHandler
    public void onLogin(final LoginEvent e) {
        Player player = e.getPlayer();

        if (player.hasPermission(JOIN_MESSAGE_PERMISSION)) {
            Component playerJoinMessage = plugin.configManager().messages().userJoin()
                .replaceText(builder -> builder.matchLiteral("<player>")
                    .replacement(player.getName()));

            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(playerJoinMessage));
        }
    }

    @EventHandler
    private void onLeave(final PlayerQuitEvent event) {
        Player player = event.getPlayer();

        plugin.usersHolder().users().stream()
            .filter(ChatUser.class::isInstance)
            .map(ChatUser.class::cast)
            .filter(user -> user.lastMessagedUser().isPresent())
            .filter(user -> user.lastMessagedUser().get().player().equals(player))
            .forEach(user -> user.lastMessagedUser(null));

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.usersHolder().removeUser(player.getUniqueId()));

        if (player.hasPermission(LEAVE_MESSAGE_PERMISSION) && AuthMeApi.getInstance().isAuthenticated(player)) {
            Component playerLeaveMessage = plugin.configManager().messages().userLeave()
                .replaceText(builder -> builder.matchLiteral("<player>")
                    .replacement(player.getName()));

            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(playerLeaveMessage));
        }
    }

}
