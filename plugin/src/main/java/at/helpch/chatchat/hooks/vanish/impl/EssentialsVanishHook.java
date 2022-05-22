package at.helpch.chatchat.hooks.vanish.impl;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.listener.EssentialsVanishListener;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class EssentialsVanishHook extends VanillaVanishHook {

    private final ChatChatPlugin plugin;

    public EssentialsVanishHook(ChatChatPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public @NotNull Optional<@NotNull List<String>> dependency() {
        return Optional.of(List.of("Essentials"));
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(new EssentialsVanishListener(plugin), plugin);
    }
}
