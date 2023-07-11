package at.helpch.chatchat.api.placeholder;

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
     * Compiles all {@link MiniPlaceholder}s into a single {@link TagResolver}.
     *
     * @param context The context in which the placeholder is used.
     * @return the compiled tags
     */
    @NotNull TagResolver compileTags(@NotNull MiniPlaceholder.Context context);

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
