package at.helpch.chatchat.user;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.User;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class UsersHolder {

    public static final User CONSOLE = ConsoleUser.INSTANCE;

    private @NotNull final Map<UUID, User> users = new HashMap<>();

    private @NotNull final Set<UUID> socialSpyUsers = new HashSet<>();

    public UsersHolder() {
        users.put(CONSOLE.uuid(), CONSOLE);
    }

    public @NotNull User getUser(@NotNull final UUID uuid) {
        return users.computeIfAbsent(uuid, ChatUserImpl::new);
    }

    public @NotNull User getUser(@NotNull final CommandSender user) {
        if (user instanceof ConsoleCommandSender) {
            return CONSOLE;
        }

        return getUser(((Player) user).getUniqueId());
    }

    public void removeUser(@NotNull final UUID uuid) {
        users.remove(uuid);
        socialSpyUsers.remove(uuid);
    }

    public void removeUser(@NotNull final Player player) {
        removeUser(player.getUniqueId());
    }

    public @NotNull ChatUser addUser(@NotNull final UUID uuid) {
        final var user = new ChatUserImpl(uuid);
        users.put(uuid, user);
        return user;
    }

    public @NotNull ChatUser addUser(@NotNull final Player player) {
        return addUser(player.getUniqueId());
    }

    public @NotNull Collection<User> users() {
        return users.values();
    }

    public @NotNull Collection<User> socialSpies() {
        return socialSpyUsers.stream().map(this::getUser).collect(Collectors.toUnmodifiableSet());
    }

    public void setSocialSpy(UUID uuid, boolean enabled) {
        if (enabled) {
            socialSpyUsers.add(uuid);
        } else {
            socialSpyUsers.remove(uuid);
        }
    }

    public boolean isSocialSpy(UUID uuid) {
        return socialSpyUsers.contains(uuid);
    }
}
