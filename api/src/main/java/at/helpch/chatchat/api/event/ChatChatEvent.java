package at.helpch.chatchat.api.event;

import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.Format;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ChatChatEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;

    private @NotNull final Player player;
    private @NotNull Audience recipients;
    private @NotNull Format format;
    private @NotNull Component message;
    private @NotNull final Channel channel;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public ChatChatEvent(
        final boolean async,
        @NotNull final Player player,
        @NotNull final Audience recipients,
        @NotNull final Format format,
        @NotNull final Component message,
        @NotNull final Channel channel
    ) {
        super(async);
        this.player = player;
        this.recipients = recipients;
        this.format = format;
        this.message = message;
        this.channel = channel;
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

    public @NotNull Player player() {
        return player;
    }

    public Audience recipients() {
        return recipients;
    }

    public void recipients(@NotNull final Audience recipients) {
        this.recipients = recipients;
    }

    public Format format() {
        return format;
    }

    public void format(@NotNull final Format format) {
        this.format = format;
    }

    public Component message() {
        return message;
    }

    public void message(@NotNull final Component message) {
        this.message = message;
    }

    public Channel channel() {
        return channel;
    }
}
