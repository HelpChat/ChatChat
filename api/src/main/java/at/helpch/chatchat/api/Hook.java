package at.helpch.chatchat.api;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/**
 * A hook to interface with another plugin.
 */
public interface Hook {
    /**
     * @return the plugin that this hook depends on
     */
    @NotNull Optional<String> dependency();

    /**
     * Enable the hook.
     */
    void enable();
}
