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

    public @NotNull ChatUser user() {
        return user;
    }

    public @NotNull Format format() {
        return format;
    }

    public void format(@NotNull final Format format) {
        this.format = format;
    }

    public @NotNull Component message() {
        return message;
    }

    public void message(@NotNull final Component message) {
        this.message = message;
    }

    public @NotNull Channel channel() {
        return channel;
    }

    public Set<User> recipients() {
        return recipients;
    }

}
