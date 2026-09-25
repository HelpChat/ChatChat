package at.helpch.chatchat.api.event;

import at.helpch.chatchat.api.format.Format;
import at.helpch.chatchat.api.user.ChatUser;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/** Called after a remote recipient is found and before a cross-server private message is sent. */
public final class CrossServerPMSendEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ChatUser sender;
    private final UUID recipientId;
    private final String recipientName;
    private final boolean reply;
    private Format senderFormat;
    private Format recipientFormat;
    private Format socialSpyFormat;
    private Component message;
    private boolean cancelled;

    public CrossServerPMSendEvent(@NotNull final ChatUser sender, @NotNull final UUID recipientId,
                                  @NotNull final String recipientName, @NotNull final Format senderFormat,
                                  @NotNull final Format recipientFormat, @NotNull final Format socialSpyFormat,
                                  @NotNull final Component message, final boolean reply) {
        this.sender = sender;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.senderFormat = senderFormat;
        this.recipientFormat = recipientFormat;
        this.socialSpyFormat = socialSpyFormat;
        this.message = message;
        this.reply = reply;
    }

    public @NotNull ChatUser sender() {
        return sender;
    }

    public @NotNull UUID recipientId() {
        return recipientId;
    }

    public @NotNull String recipientName() {
        return recipientName;
    }

    public boolean reply() {
        return reply;
    }

    public @NotNull Format senderFormat() {
        return senderFormat;
    }

    public void senderFormat(@NotNull final Format format) {
        senderFormat = format;
    }

    public @NotNull Format recipientFormat() {
        return recipientFormat;
    }

    public void recipientFormat(@NotNull final Format format) {
        recipientFormat = format;
    }

    public @NotNull Format socialSpyFormat() {
        return socialSpyFormat;
    }

    public void socialSpyFormat(@NotNull final Format format) {
        socialSpyFormat = format;
    }

    public @NotNull Component message() {
        return message;
    }

    public void message(@NotNull final Component newMessage) {
        message = newMessage;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
