package at.helpch.chatchat.user;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.format.Format;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import at.helpch.chatchat.cache.ExpiringCache;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class ChatUserImpl implements ChatUser {

    public ChatUserImpl(@NotNull final UUID uuid) {
        this.uuid = uuid;
    }

    private final ExpiringCache<ChatUser> lastMessagedUser =
        new ExpiringCache<>(ChatChatPlugin.cacheDuration(), TimeUnit.SECONDS);

    private final UUID uuid;
    private Channel channel;
    // TODO: 8/9/22 Remove unused field!
    private Format format;
    private boolean privateMessages = true;
    private boolean personalMentions = true;
    private boolean channelMentions = true;
    private boolean socialSpy = false;
    private boolean chatEnabled = true;
    private Set<UUID> ignoredUsers = new HashSet<>();

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
    public boolean hasPermission(@NotNull final String node) {
        return player().hasPermission(node);
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
    public boolean personalMentions() {
        return personalMentions;
    }

    @Override
    public void personalMentions(boolean receivesPersonalMentions) {
        this.personalMentions = receivesPersonalMentions;
    }

    @Override
    public boolean channelMentions() {
        return channelMentions;
    }

    @Override
    public void channelMentions(boolean receivesChannelMentions) {
        this.channelMentions = receivesChannelMentions;
    }

    public void socialSpy(final boolean enabled) {
        socialSpy = enabled;
    }

    @Override
    public boolean socialSpy() {
        return socialSpy;
    }

    @Override
    public @NotNull Set<UUID> ignoredUsers() {
        return ignoredUsers;
    }

    @Override
    public void ignoredUsers(@NotNull Set<UUID> users) {
        this.ignoredUsers = users;
    }

    @Override
    public void ignoreUser(@NotNull User user) {
        ignoredUsers.add(user.uuid());
    }

    @Override
    public void unignoreUser(@NotNull User user) {
        ignoredUsers.remove(user.uuid());
    }

    @Override
    public void chatState(final boolean enabled) {
        this.chatEnabled = enabled;
    }

    @Override
    public boolean chatEnabled() {
        return this.chatEnabled;
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
