package at.helpch.chatchat.api.hook;

import at.helpch.chatchat.api.ChatChatAPI;

import java.util.Set;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

public interface HookManager {
    boolean addHook(@NotNull final Function<ChatChatAPI, ? extends Hook> constructor);
    @NotNull Set<Hook> hooks();
    @NotNull Set<VanishHook> vanishHooks();
}
