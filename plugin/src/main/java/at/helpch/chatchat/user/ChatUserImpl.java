package at.helpch.chatchat.user;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.Format;
import at.helpch.chatchat.api.User;
import at.helpch.chatchat.cache.ExpiringCache;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ChatUserImpl implements ChatUser {

    public ChatUserImpl(@NotNull final UUID uuid) {
        this.uuid = uuid;
    }

    private final ExpiringCache<ChatUser> lastMessagedUser = new ExpiringCache<>(5, TimeUnit.MINUTES);

    private final UUID uuid;
    private Channel channel;
    private Format format;
    private boolean privateMessages = true;

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
        return lastMessagedUser.get();
    }

    @Override
    public void lastMessagedUser(@Nullable final ChatUser user) {
        lastMessagedUser.put(user);
    }

    @Override
    public boolean privateMessages() {
        return privateMessages;
    }

    @Override
    public void privateMessages(final boolean enabled) {
        this.privateMessages = enabled;
    }

    @Override
    public boolean canSee(@NotNull final User target) {
        if (!(target instanceof ChatUser)) {
            return true;
        }

        final var chatUser = (ChatUser) target;

        final var plugin = JavaPlugin.getPlugin(ChatChatPlugin.class);

        return plugin.hookManager()
            .vanishHooks()
            .stream()
            .filter(Objects::nonNull)
            .allMatch(hook -> hook.canSee(this, chatUser));
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
