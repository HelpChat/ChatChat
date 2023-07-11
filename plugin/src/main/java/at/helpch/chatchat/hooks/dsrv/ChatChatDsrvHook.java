package at.helpch.chatchat.hooks.dsrv;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.hooks.AbstractInternalHook;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class ChatChatDsrvHook extends AbstractInternalHook {

    private static final String DISCORD_SRV = "DiscordSRV";

    private final ChatChatPlugin plugin;

    public ChatChatDsrvHook(@NotNull final ChatChatPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        return plugin.configManager().extensions().addons().discordSrvChannelsBridging() &&
            Bukkit.getPluginManager().isPluginEnabled(DISCORD_SRV);
    }

    @Override
    public @NotNull String name() {
        return DISCORD_SRV + "Hook";
    }

    @Override
    public void enable() {
        final var hook = new DsrvListener(plugin);
        DiscordSRV.getPlugin().getPluginHooks().add(hook);
        Bukkit.getPluginManager().registerEvents(hook, plugin);
    }

}
