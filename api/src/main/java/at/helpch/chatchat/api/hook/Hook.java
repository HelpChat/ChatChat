package at.helpch.chatchat.api.hook;

import org.jetbrains.annotations.NotNull;

/**
 * A hook to interface with another plugin.
 */
public interface Hook {
    /**
     * @return true if ChatChat should register the hook, false otherwise.
     */
    boolean register();

    /**
     * The name of the hook should be formatted as "plugin-name:hook-name". E.g. "ChatChat:TownyHook". The plugin name,
     * should be the name of the plugin that the hook is implemented in. The hook name should also follow this regex
     * pattern: [a-zA-Z0-9_]+
     *
     * @return the name of the hook. this will be used as an identifier, so if there will be multiple hooks with the
     * same name, only one of them will be registered.
     */
    @NotNull String name();

    /**
     * Enable the hook.
     */
    void enable();
}
