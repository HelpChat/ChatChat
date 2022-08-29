package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatAPIImpl;
import at.helpch.chatchat.api.user.ChatUser;
import de.myzelyam.api.vanish.PlayerHideEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class SuperVanishListener implements Listener {
    private final ChatChatAPIImpl api;

    public SuperVanishListener(@NotNull final ChatChatAPIImpl api) {
        this.api = api;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVanish(@NotNull final PlayerHideEvent event) {
        // find everyone who last messaged the person being vanished, and remove their reference
        api.usersHolder().users().stream()
            .filter(user -> user instanceof ChatUser)
            .map(user -> (ChatUser) user)
            .filter(user -> user.lastMessagedUser().isPresent())
            .filter(user -> user.lastMessagedUser().get().player().equals(event.getPlayer()))
            .filter(user -> !user.canSee(user.lastMessagedUser().get()))
            .forEach(user -> user.lastMessagedUser(null));
    }
}
