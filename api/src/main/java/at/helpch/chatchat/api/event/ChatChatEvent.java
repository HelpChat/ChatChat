package at.helpch.chatchat.api.event;

import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.format.Format;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * <p>
 *     Called whenever a {@link ChatUser} sends a chat message.
 * </p>
 */
public class ChatChatEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;

    private @NotNull final ChatUser user;
    private @NotNull Format format;
    private @NotNull Component message;
    private @NotNull final Channel channel;
    private @NotNull final Set<User> recipients;

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    public ChatChatEvent(
        final boolean async,
        @NotNull final ChatUser user,
        @NotNull final Format format,
        @NotNull final Component message,
        @NotNull final Channel channel,
        @NotNull Set<User> recipients) {
        super(async);
        this.user = user;
        this.format = format;
        this.message = message;
        this.channel = channel;
        this.recipients = recipients;
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
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Get the user that sent the message.
     *
     * @return The user that sent the message.
     */
    public @NotNull ChatUser user() {
        return user;
    }

    /**
     * Get the format that will be used to format the message for the sender and recipients.
     *
     * @return The format that will be used.
     */
    public @NotNull Format format() {
        return format;
    }

    /**
     * Change the format that will be used to format the message for the sender and recipients.
     *
     * @param format The format that will be used.
     */
    public void format(@NotNull final Format format) {
        this.format = format;
    }

    /**
     * Get the message that is being sent.
     *
     * @return The message that is being sent.
     */
    public @NotNull Component message() {
        return message;
    }

    /**
     * Change the message that will be received.
     *
     * @param message The new message.
     */
    public void message(@NotNull final Component message) {
        this.message = message;
    }

    /**
     * Get the channel that the message was sent in. This will not always be the channel that the user is in as the
     * message can be sent using a channel prefix.
     *
     * @return The channel that the message was sent in.
     */
    public @NotNull Channel channel() {
        return channel;
    }

    /**
     * Get a mutable set of users that represent the recipients of the message. Updating the {@link Set} will affect the
     * recipients that will end up seeing the message. Also, recipients do not have to be part of the
     * {@link ChatChatEvent#channel()}.
     *
     * @return The recipients of the message.
     */
    public Set<User> recipients() {
        return recipients;
    }

}
