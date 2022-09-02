package at.helpch.chatchat.api.rule;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * A manager for rules. This class is used to add and register new rules.
 */
public interface RuleManager {
    /**
     * Adds and registers a new {@link Rule} with the manager. The rule will only apply to public messages.
     *
     * @param rule The rule to add.
     */
    void addPublicChatRule(@NotNull final Rule rule);

    /**
     * Adds and registers a new {@link Rule} with the manager. The rule will only apply to private messages.
     *
     * @param rule The rule to add.
     */
    void addPrivateChatRule(@NotNull final Rule rule);

    /**
     * Get all public chat rules.
     *
     * @return An unmodifiable {@link Set} of all registered public chat {@link Rule}s.
     */
    @NotNull Set<Rule> publicChatRules();

    /**
     * Get all private chat rules.
     *
     * @return An unmodifiable {@link Set} of all registered private chat {@link Rule}s.
     */
    @NotNull Set<Rule> privateChatRules();
}
