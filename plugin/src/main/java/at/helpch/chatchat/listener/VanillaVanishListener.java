package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatAPIImpl;
import at.helpch.chatchat.api.user.ChatUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerHideEntityEvent;
import org.jetbrains.annotations.NotNull;

public class VanillaVanishListener implements Listener {

    private final ChatChatAPIImpl api;

    public VanillaVanishListener(@NotNull final ChatChatAPIImpl api) {
        this.api = api;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onVanish(final PlayerHideEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final var chatUser = (ChatUser) api.usersHolder().getUser(event.getPlayer());

        final var lastMessaged = chatUser.lastMessagedUser();
        if (lastMessaged.isEmpty()) {
            return;
        }

        if (!lastMessaged.get().uuid().equals(event.getEntity().getUniqueId())) {
            return;
        }

        if (chatUser.canSee(lastMessaged.get())) {
            return;
        }

        chatUser.lastMessagedUser(null);
    }
}
