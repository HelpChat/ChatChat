package at.helpch.chatchat.hooks;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.hook.VanishHook;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractInternalVanishHook extends VanishHook {

    private final ChatChatPlugin plugin;

    public AbstractInternalVanishHook(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull ChatChatPlugin plugin() {
        return plugin;
    }
}
