package at.helpch.chatchat.api.placeholder;

import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * A manager for . This class is used to add and register new mention types.
 */
public interface MiniPlaceholderManager {
    /**
     * Adds and registers a new {@link MiniPlaceholder} with the manager.
     *
     * @param placeholder the placeholder to register
     */
    void addPlaceholder(@NotNull MiniPlaceholder placeholder);

    /**
     * Compiles all {@link MiniPlaceholder}s into a {@link TagResolver}.
     *
     * @param inMessage whether the tag resolver is used in a message or in a format
     * @param sender the sender of the message
     * @param recipient the recipient of the message
     * @return the compiled tags
     */
    @NotNull TagResolver compileTags(boolean inMessage, @NotNull ChatUser sender, @NotNull User recipient);

    /**
     * Get all placeholders.
     *
     * @return An unmodifiable {@link Set} of all registered {@link MiniPlaceholder}s.
     */
    @NotNull Set<@NotNull MiniPlaceholder> placeholders();

    /**
     * Clears all placeholders.
     */
    public void clear();
}
