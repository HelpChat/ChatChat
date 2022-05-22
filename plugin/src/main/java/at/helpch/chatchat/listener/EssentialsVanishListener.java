package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class EssentialsVanishListener implements Listener {

    private final ChatChatPlugin plugin;

    public EssentialsVanishListener(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVanish(@NotNull final VanishStatusChangeEvent event) {
        // find everyone who last messaged the person leaving, and remove their reference
        plugin.usersHolder().users().stream()
            .filter(user -> user instanceof ChatUser)
            .map(user -> (ChatUser) user)
            .filter(user -> user.lastMessagedUser().isPresent())
            .filter(user -> user.lastMessagedUser().get().player().equals(event.getAffected().getBase()))
            .forEach(user -> user.lastMessagedUser(null));
    }
}