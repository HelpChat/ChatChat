package at.helpch.chatchat.listener;

import at.helpch.chatchat.api.event.ChatChatEvent;
import at.helpch.chatchat.api.user.ChatUser;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class GriefPreventionSoftMuteListener implements Listener {

    public GriefPreventionSoftMuteListener() {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onChat(@NotNull final ChatChatEvent event) {
        if (!GriefPrevention.instance.dataStore.isSoftMuted(event.user().uuid())) {
            return;
        }

        event.recipients().removeIf(recipient -> recipient instanceof ChatUser && !GriefPrevention.instance.dataStore.isSoftMuted(recipient.uuid()));
    }
}
