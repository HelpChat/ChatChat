package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import de.myzelyam.api.vanish.PlayerHideEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class SuperVanishListener implements Listener {
    private final ChatChatPlugin plugin;

    public SuperVanishListener(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVanish(@NotNull final PlayerHideEvent event) {
        final var hiddenPlayerId = event.getPlayer().getUniqueId();

        // find everyone who last messaged the person being vanished, and remove their reference
        plugin.usersHolder().users().stream()
            .filter(user -> user instanceof ChatUser)
            .map(user -> (ChatUser) user)
            .filter(user -> user.lastMessagedUser()
                .filter(lastMessaged -> lastMessaged.uuid().equals(hiddenPlayerId))
                .filter(lastMessaged -> !user.canSee(lastMessaged))
                .isPresent())
            .forEach(user -> user.lastMessagedUser(null));
    }
}
