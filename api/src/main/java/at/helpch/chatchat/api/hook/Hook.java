package at.helpch.chatchat.api.hook;

import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/**
 * A hook to interface with another plugin.
 */
public interface Hook {
    /**
     * @return the plugin that this hook depends on
     */
    @NotNull Optional<@NotNull List<String>> dependency();

    /**
     * Enable the hook.
     */
    void enable();
}
