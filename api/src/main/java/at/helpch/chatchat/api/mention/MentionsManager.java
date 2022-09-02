package at.helpch.chatchat.api.mention;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * A manager for mentions. This class is used to add and register new mention types.
 */
public interface MentionsManager {

    /**
     * Adds and registers a new {@link Mention} with the manager.
     *
     * @param mention The mention to register.
     *
     */
    void addMention(@NotNull final Mention mention);

    /**
     * Get all mentions.
     *
     * @return An unmodifiable {@link Set} of all registered basic {@link Mention}s.
     */
    @NotNull Set<Mention> mentions();
}
