package at.helpch.chatchat.user;

import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.format.Format;
import at.helpch.chatchat.api.user.User;
import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.format.ChatFormat;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public final class ConsoleUser implements User {

    public static final ConsoleUser INSTANCE = new ConsoleUser();

    private ConsoleUser() {
    }

    @Override
    public @NotNull Channel channel() {
        return ChatChannel.defaultChannel();
    }

    @Override
    public void channel(@NotNull final Channel channel) {
    }

    @Override
    public @NotNull Format format() {
        return ChatFormat.defaultFormat();
    }

    @Override
    public void format(@NotNull final Format format) {
    }

    @Override
    public @NotNull UUID uuid() {
        return Identity.nil().uuid();
    }

    @Override
    public boolean hasPermission(@NotNull final String node) {
        return true;
    }

    @Override
    public boolean canSee(@NotNull final User target) {
        return true;
    }

    @Override
    public @NotNull Set<UUID> ignoredUsers() {
        return Set.of();
    }

    @Override
    public void ignoredUsers(@NotNull final Set<UUID> users) {
    }

    @Override
    public void ignoreUser(@NotNull final User user) {
    }

    @Override
    public void unignoreUser(@NotNull final User user) {
    }

    @Override
    public boolean chatEnabled() {
        return true;
    }

    @Override
    public void chatState(final boolean enabled) {
    }

    @Override
    public @NotNull Audience audience() {
        return Bukkit.getConsoleSender();
    }

    @Override
    public @NotNull Identity identity() {
        return Identity.nil();
    }

}
