package at.helpch.chatchat.api.hook;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/**
 * A hook to interface with another plugin.
 */
public interface Hook {
    /**
     * @return true if ChatChat should register the hook or false otherwise.
     */
    boolean register();

    /**
     * @return the name of the hook. this is only going to be used for display purposes and not for anything internally.
     */
    @NotNull Optional<@NotNull String> name();

    /**
     * Enable the hook.
     */
    void enable();
}
