package at.helpch.chatchat.user;

import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.Format;
import at.helpch.chatchat.api.User;

import java.util.Objects;
import java.util.UUID;

import at.helpch.chatchat.util.ChannelUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ChatUser implements User {

    public ChatUser(@NotNull final UUID uuid) {
        this.uuid = uuid;
    }

    private @NotNull final UUID uuid;
    private Channel channel;
    private Format format;

    @Override
    public @NotNull Channel channel() {
        return channel;
    }

    @Override
    public void channel(@NotNull final Channel channel) {
        this.channel = channel;
    }

    public @NotNull Format format() {
        return format;
    }

    public void format(@NotNull final Format format) {
        this.format = format;
    }

    public @NotNull UUID uuid() {
        return uuid;
    }

    public boolean canSee(@NotNull final Channel channel) {
        return player().hasPermission(ChannelUtils.SEE_CHANNEL_PERMISSION + channel.commandName());
    }

    public boolean canUse(@NotNull final Channel channel) {
        return player().hasPermission(ChannelUtils.USE_CHANNEL_PERMISSION + channel.commandName());
    }

    public @NotNull Player player() {
        return Objects.requireNonNull(Bukkit.getPlayer(uuid)); // this will never be null
    }

    @Override
    public String toString() {
        return "ChatUser{" +
                "uuid=" + uuid +
                ", channel=" + channel +
                ", format=" + format +
                '}';
    }
}
