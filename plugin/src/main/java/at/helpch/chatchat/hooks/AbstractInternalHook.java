package at.helpch.chatchat.hooks;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.hook.Hook;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractInternalHook implements Hook {

    private final ChatChatPlugin plugin;

    public AbstractInternalHook(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull ChatChatPlugin plugin() {
        return plugin;
    }
}
