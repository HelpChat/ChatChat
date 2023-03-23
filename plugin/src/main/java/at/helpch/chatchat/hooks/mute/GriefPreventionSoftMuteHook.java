package at.helpch.chatchat.hooks.mute;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.hooks.AbstractInternalMuteHook;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * <p>
 *     Mute Hook that uses Grief Prevention's implementation of a soft mute.
 * </p>
 */
public class GriefPreventionSoftMuteHook extends AbstractInternalMuteHook {

    private static final String GRIEF_PREVENTION = "GriefPrevention";

    public GriefPreventionSoftMuteHook(final @NotNull ChatChatPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean register() {
        return Bukkit.getPluginManager().isPluginEnabled(GRIEF_PREVENTION);
    }

    @Override
    public @NotNull String name() {
        return GRIEF_PREVENTION + "Hook";
    }

    @Override
    public boolean isMuted(final @NotNull ChatUser user) {
        return GriefPrevention.instance.dataStore.isSoftMuted(user.uuid());
    }

}
