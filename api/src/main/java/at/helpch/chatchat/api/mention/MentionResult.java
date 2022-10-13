package at.helpch.chatchat.api.mention;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link Mention} result.
 */
public interface MentionResult {

    /**
     * Get the original message with the mentions processed.
     *
     * @return The processed message.
     */
    @NotNull Component message();

    /**
     * This tells you if the target was mentioned during the mention processing or not.
     *
     * @return true if the target was mentioned, false otherwise.
     */
    boolean mentioned();

    /**
     * This decides if the target will hear a sound when they'll receive the message or not.
     *
     * @return true if the target should hear a sound, false otherwise.
     */
    boolean playSound();
}
