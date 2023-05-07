package at.helpch.chatchat.hooks.gp;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.hooks.AbstractInternalHook;
import at.helpch.chatchat.listener.GriefPreventionSoftMuteListener;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * Mute Hook that uses Grief Prevention's implementation of a soft mute.
 * </p>
 */
public class GriefPreventionSoftMuteHook extends AbstractInternalHook {

    private static final String GRIEF_PREVENTION = "GriefPrevention";

    private final ChatChatPlugin plugin;

    public GriefPreventionSoftMuteHook(final @NotNull ChatChatPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        return plugin.configManager().extensions().addons().griefPreventionSoftMute() &&
            Bukkit.getPluginManager().isPluginEnabled(GRIEF_PREVENTION);
    }

    @Override
    public @NotNull String name() {
        return GRIEF_PREVENTION + "Hook";
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(new GriefPreventionSoftMuteListener(), plugin);
    }

}
