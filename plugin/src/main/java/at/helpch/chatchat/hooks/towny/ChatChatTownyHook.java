package at.helpch.chatchat.hooks.towny;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.hooks.AbstractInternalHook;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ChatChatTownyHook extends AbstractInternalHook {

    private static final String TOWNY = "Towny";

    private final ChatChatPlugin plugin;

    public ChatChatTownyHook(@NotNull final ChatChatPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        return plugin.configManager().extensions().addons().townyChannels() &&
            Bukkit.getPluginManager().isPluginEnabled(TOWNY);
    }

    @Override
    public @NotNull String name() {
        return TOWNY + "Hook";
    }

    @Override
    public void enable() {
        plugin.channelTypeRegistry().add("TOWNY_TOWN", TownyTownChannel::new);
        plugin.channelTypeRegistry().add("TOWNY_NATION", TownyNationChannel::new);
    }
}
