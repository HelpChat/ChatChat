package at.helpch.chatchat.hooks.dsrv;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.hook.Hook;
import github.scarsz.discordsrv.DiscordSRV;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class ChatChatDsrvHook implements Hook {
    private static final String DISCORD_SRV = "DiscordSRV";

    private final ChatChatPlugin plugin;

    public ChatChatDsrvHook(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        return Bukkit.getPluginManager().isPluginEnabled(DISCORD_SRV);
    }

    @Override
    public @NotNull Optional<@NotNull String> name() {
        if (register()) return Optional.of(DISCORD_SRV);
        return Optional.empty();
    }

    @Override
    public void enable() {
        final var hook = new DsrvListener(plugin);
        DiscordSRV.getPlugin().getPluginHooks().add(hook);
        Bukkit.getPluginManager().registerEvents(hook, plugin);
    }

}
