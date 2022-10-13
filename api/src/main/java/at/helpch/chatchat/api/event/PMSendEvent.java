package at.helpch.chatchat.api.event;

import at.helpch.chatchat.api.format.Format;
import at.helpch.chatchat.api.user.ChatUser;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a {@link ChatUser} sends a private message to another {@link ChatUser}.
 */
public class PMSendEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;

    private @NotNull final ChatUser sender;
    private @NotNull final ChatUser recipient;
    private @NotNull Format senderFormat;
    private @NotNull Format recipientFormat;
    private @NotNull Component message;
    private final boolean reply;
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    public PMSendEvent(
        @NotNull final ChatUser sender,
        @NotNull final ChatUser recipient,
        @NotNull final Format senderFormat,
        @NotNull final Format recipientFormat,
        @NotNull final Component message,
        final boolean reply
    ) {
        this.sender = sender;
        this.recipient = recipient;
        this.senderFormat = senderFormat;
        this.recipientFormat = recipientFormat;
        this.message = message;
        this.reply = reply;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        cancelled = cancel;
    }

    /**
     * Get the {@link ChatUser} that sent the message.
     *
     * @return the {@link ChatUser} that sent the message.
     */
    public @NotNull ChatUser sender() {
        return sender;
    }

    /**
     * Get the {@link ChatUser} that will receive the message.
     *
     * @return the {@link ChatUser} that will receive the message.
     */
    public @NotNull ChatUser recipient() {
        return recipient;
    }

    /**
     * Get the {@link Format} that will be used to format the message that the sender sees.
     *
     * @return the {@link Format} that will be used to format the message that the sender sees.
     */
    public @NotNull Format senderFormat() {
        return senderFormat;
    }

    /**
     * Change the {@link Format} that will be used to format the message that the sender sees.
     *
     * @param format the new {@link Format} that will be used to format the message that the sender sees.
     */
    public void senderFormat(@NotNull final Format format) {
        this.senderFormat = format;
    }

    /**
     * Get the {@link Format} that will be used to format the message that the recipient sees.
     *
     * @return the {@link Format} that will be used to format the message that the recipient sees.
     */
    public @NotNull Format recipientFormat() {
        return recipientFormat;
    }

    /**
     * Change the {@link Format} that will be used to format the message that the recipient sees.
     *
     * @param format the new {@link Format} that will be used to format the message that the recipient sees.
     */
    public void recipientFormat(@NotNull final Format format) {
        this.recipientFormat = format;
    }

    /**
     * Get the message that the sender sent.
     *
     * @return the message that the sender sent.
     */
    public @NotNull Component message() {
        return message;
    }

    /**
     * Change the message that both the sender and the recipient will see.
     *
     * @param message the new message that both the sender and the recipient will see.
     */
    public void message(@NotNull final Component message) {
        this.message = message;
    }

    /**
     * A private message is a reply when the sender used one of the reply commands like /r. This will not return true
     * if the {@link ChatUser#lastMessagedUser()} of the sender is the recipient unless the sender used a reply command.
     *
     * @return true if the message is a reply, false otherwise.
     */
    public boolean reply() {
        return reply;
    }
}
