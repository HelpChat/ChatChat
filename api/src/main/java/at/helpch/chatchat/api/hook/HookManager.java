package at.helpch.chatchat.api.hook;

import at.helpch.chatchat.api.ChatChatAPI;

import java.util.Set;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

/**
 * A manager for hooks. This class is used to add and register new hooks.
 */
public interface HookManager {

    /**
     * Adds and registers a new {@link Hook} with the manager.
     * <br>
     * Registration will fail if the hook name is invalid (Check {@link Hook#name()} for valid hook name), if a hook
     * with the same name is already registered, if the hook throws an unhandled exception during registration or if the
     * hook returns false from {@link Hook#register()}.
     *
     * @param constructor A method, lambda or a constructor that takes in a {@link ChatChatAPI} parameter and returns
     *                    a {@link Hook}.
     *
     * @return False if the registration failed, true otherwise.
     */
    boolean addHook(@NotNull final Function<ChatChatAPI, ? extends Hook> constructor);

    /**
     * Get all basic hooks.
     *
     * @return An unmodifiable {@link Set} of all registered basic {@link Hook}s.
     */
    // TODO: 8/29/22 create a BasicHook
    @NotNull Set<Hook> hooks();

    /**
     * Get all vanish hooks.
     *
     * @return An unmodifiable {@link Set} of all registered {@link VanishHook}s.
     */
    @NotNull Set<VanishHook> vanishHooks();
}
