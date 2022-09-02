package at.helpch.chatchat.hooks.dsrv;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.hook.Hook;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class ChatChatDsrvHook implements Hook {
    private static final String DISCORD_SRV = "DiscordSRV";

    private final ChatChatPlugin plugin;
    private final DsrvListener hook;

    public ChatChatDsrvHook(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
        this.hook = new DsrvListener(plugin);
    }

    @Override
    public boolean register() {
        return Bukkit.getPluginManager().isPluginEnabled(DISCORD_SRV);
    }

    @Override
    public @NotNull String name() {
        return "ChatChat:" + DISCORD_SRV + "Hook";
    }

    @Override
    public void enable() {
        DiscordSRV.getPlugin().getPluginHooks().add(hook);
        Bukkit.getPluginManager().registerEvents(hook, plugin);
    }
}
