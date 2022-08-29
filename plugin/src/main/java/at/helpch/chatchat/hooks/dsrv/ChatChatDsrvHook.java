package at.helpch.chatchat.hooks.dsrv;

import at.helpch.chatchat.ChatChatAPIImpl;
import at.helpch.chatchat.api.ChatChatAPI;
import at.helpch.chatchat.api.hook.Hook;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class ChatChatDsrvHook implements Hook {
    private static final String DISCORD_SRV = "DiscordSRV";

    private final ChatChatAPIImpl api;

    public ChatChatDsrvHook(@NotNull final ChatChatAPI api) {
        if (!(api instanceof ChatChatAPIImpl)) {
            throw new IllegalArgumentException("api must be an instance of ChatChatAPIImpl");
        }

        this.api = (ChatChatAPIImpl) api;
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
        final var hook = new DsrvListener(api);
        DiscordSRV.getPlugin().getPluginHooks().add(hook);
        Bukkit.getPluginManager().registerEvents(hook, api.plugin());
    }

}
