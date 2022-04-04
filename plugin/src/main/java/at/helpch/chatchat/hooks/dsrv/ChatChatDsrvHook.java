package at.helpch.chatchat.hooks.dsrv;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Hook;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class ChatChatDsrvHook implements Hook {

    private final ChatChatPlugin plugin;

    public ChatChatDsrvHook(final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String dependency() {
        return "DiscordSRV";
    }

    @Override
    public void enable() {
        final var hook = new DsrvListener(plugin);
        DiscordSRV.getPlugin().getPluginHooks().add(hook);
        Bukkit.getPluginManager().registerEvents(hook, plugin);
    }

}
