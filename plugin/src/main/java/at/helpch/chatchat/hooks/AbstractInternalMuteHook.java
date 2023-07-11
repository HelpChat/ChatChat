package at.helpch.chatchat.hooks;


import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.hook.MuteHook;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractInternalMuteHook extends MuteHook {

    private final ChatChatPlugin plugin;

    public AbstractInternalMuteHook(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull ChatChatPlugin plugin() {
        return plugin;
    }
}
