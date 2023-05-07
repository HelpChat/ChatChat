package at.helpch.chatchat.api.hook;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * A hook to interface with another plugin.
 */
public interface Hook {

    /**
     * The plugin that registered the hook.
     *
     * @return the plugin that registered the hook
     */
    @NotNull Plugin plugin();

    /**
     * @return true if ChatChat should register the hook, false otherwise.
     */
    boolean register();

    /**
     * The hook name should follow this regex pattern: [a-zA-Z0-9_]+
     *
     * @return the name of the hook. this will be used as an identifier, so if there will be multiple hooks with the
     * same name, only one of them will be registered.
     */
    @NotNull String name();

    /**
     * Enable the hook.
     */
    default void enable() {}

    /**
     * Disable the hook.
     */
    default void disable() {}
}
