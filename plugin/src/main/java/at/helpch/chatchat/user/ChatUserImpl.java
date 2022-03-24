package at.helpch.chatchat.user;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.Format;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.util.ChannelUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ChatUserImpl implements ChatUser {

    public ChatUserImpl(@NotNull final UUID uuid) {
        this.uuid = uuid;
    }

    private @NotNull final UUID uuid;
    private ChatUser lastMessaged;
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

    @Override
    public @NotNull Format format() {
        return format;
    }

    @Override
    public void format(@NotNull final Format format) {
        this.format = format;
    }

    @Override
    public @NotNull UUID uuid() {
        return uuid;
    }

    @Override
    public @NotNull Optional<ChatUser> lastMessagedUser() {
        return Optional.ofNullable(lastMessaged);
    }

    @Override
    public void lastMessagedUser(@Nullable final ChatUser user) {
        this.lastMessaged = user;
    }

    @Override
    public @NotNull Player player() {
        return Objects.requireNonNull(Bukkit.getPlayer(uuid)); // this will never be null
    }

    @Override
    public @NotNull Audience audience() {
        return ChatChatPlugin.audiences().player(uuid);
    }

    @Override
    public @NotNull Identity identity() {
        return Identity.identity(uuid);
    }

    @Override
    public String toString() {
        return "ChatUserImpl{" +
                "uuid=" + uuid +
                ", lastMessaged=" + lastMessagedUser().map(ChatUser::uuid) +
                ", channel=" + channel +
                ", format=" + format +
                '}';
    }
}
