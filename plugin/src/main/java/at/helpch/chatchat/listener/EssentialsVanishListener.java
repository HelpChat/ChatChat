package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
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
    private void onVanish(@NotNull final VanishStatusChangeEvent event) {
        final var chatUser = (ChatUser) plugin.usersHolder().getUser(event.getAffected().getBase());

        final var lastMessaged = chatUser.lastMessagedUser();
        if (lastMessaged.isEmpty()) {
            return;
        }

        if (!lastMessaged.get().uuid().equals(event.getAffected().getUUID())) {
            return;
        }

        if (chatUser.canSee(lastMessaged.get())) {
            return;
        }

        chatUser.lastMessagedUser(null);
    }
}
