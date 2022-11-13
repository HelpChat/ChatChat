package at.helpch.chatchat.api.rule;

import at.helpch.chatchat.api.user.User;

import java.util.Optional;

import at.helpch.chatchat.api.user.ChatUser;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * A hook to interface with another plugin.
 */
public interface Rule {
    /**
     * This method is called when a {@link ChatUser} sends a public message. The rule should check if the message is
     * allowed or not. If the message is not allowed, the message event will be cancelled and the user will be notified
     * either with the message given by {@link #publicDeniedMessage()} or the invalid-message from ChatChat.
     *
     * @param sender The sender of the message.
     * @param message The message the user sent.
     *
     * @return true if the message sent by the player is allowed, false otherwise.
     */
    boolean isAllowedPublic(@NotNull ChatUser sender, @NotNull String message);

    /**
     * This method is called when a {@link ChatUser} sends a private message. The rule should check if the message is
     * allowed or not. If the message is not allowed, the message event will be cancelled and the user will be notified
     * either with the message given by {@link #privateDeniedMessage()} or the invalid-message from ChatChat.
     *
     * @param sender The sender of the message.
     * @param recipient The recipient of the message.
     * @param message The message the user sent.
     *
     * @return true if the message sent by the player is allowed, false otherwise.
     */
    boolean isAllowedPrivate(@NotNull ChatUser sender, @NotNull User recipient, @NotNull String message);

    /**
     * @return the message that should be sent to the player if the public message they sent did not respect this rule.
     */
    @NotNull default Optional<@NotNull Component> publicDeniedMessage() {
        return Optional.empty();
    }

    /**
     * @return the message that should be sent to the player if the private message they sent did not respect this rule.
     */
    @NotNull default Optional<@NotNull Component> privateDeniedMessage() {
        return Optional.empty();
    }
}
