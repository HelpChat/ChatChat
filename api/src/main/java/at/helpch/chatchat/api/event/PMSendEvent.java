package at.helpch.chatchat.api.event;

import at.helpch.chatchat.api.format.BasicFormat;
import at.helpch.chatchat.api.user.ChatUser;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PMSendEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;

    private @NotNull final ChatUser sender;
    private @NotNull final ChatUser recipient;
    private @NotNull BasicFormat senderFormat;
    private @NotNull BasicFormat recipientFormat;
    private @NotNull Component message;
    private final boolean reply;
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    public PMSendEvent(
        @NotNull final ChatUser sender,
        @NotNull final ChatUser recipient,
        @NotNull final BasicFormat senderFormat,
        @NotNull final BasicFormat recipientFormat,
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

    public @NotNull ChatUser sender() {
        return sender;
    }

    public @NotNull ChatUser recipient() {
        return recipient;
    }

    public @NotNull BasicFormat senderFormat() {
        return senderFormat;
    }

    public void senderFormat(@NotNull final BasicFormat format) {
        this.senderFormat = format;
    }

    public @NotNull BasicFormat recipientFormat() {
        return recipientFormat;
    }

    public void recipientFormat(@NotNull final BasicFormat format) {
        this.recipientFormat = format;
    }

    public @NotNull Component message() {
        return message;
    }

    public void message(@NotNull final Component message) {
        this.message = message;
    }

    public boolean reply() {
        return reply;
    }
}
