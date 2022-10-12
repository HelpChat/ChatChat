package at.helpch.chatchat.hooks.towny;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.hook.Hook;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ChatChatTownyHook implements Hook {

    private static final String TOWNY = "Towny";

    private final ChatChatPlugin plugin;

    public ChatChatTownyHook(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        return Bukkit.getPluginManager().isPluginEnabled(TOWNY);
    }

    @Override
    public @NotNull String name() {
        return "ChatChat:" + TOWNY + "Hook";
    }

    @Override
    public void enable() {
        plugin.channelTypeRegistry().add("TOWNY_TOWN", TownyTownChannel::new);
        plugin.channelTypeRegistry().add("TOWNY_NATION", TownyNationChannel::new);
    }
}
