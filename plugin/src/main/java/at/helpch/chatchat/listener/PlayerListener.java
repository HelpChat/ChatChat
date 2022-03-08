package at.helpch.chatchat.listener;

import at.helpch.chatchat.user.UsersHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final UsersHolder usersHolder;

    public PlayerListener(final UsersHolder usersHolder) {
        this.usersHolder = usersHolder;
    }

    @EventHandler
    private void onJoin(final PlayerJoinEvent event) {
        usersHolder.addUser(event.getPlayer());
    }

    @EventHandler
    private void onLeave(final PlayerQuitEvent event) {
        usersHolder.removeUser(event.getPlayer());
    }
}
