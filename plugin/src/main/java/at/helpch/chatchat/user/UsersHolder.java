package at.helpch.chatchat.user;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import at.helpch.chatchat.api.User;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class UsersHolder {

    public static final User CONSOLE = new ConsoleUser();

    private @NotNull final Map<UUID, User> users = new HashMap<>();

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
    }

    public void removeUser(@NotNull final Player player) {
        removeUser(player.getUniqueId());
    }

    public @NotNull User addUser(@NotNull final UUID uuid) {
        final var user = new ChatUserImpl(uuid);
        users.put(uuid, user);
        return user;
    }

    public @NotNull User addUser(@NotNull final Player player) {
        return addUser(player.getUniqueId());
    }

    public @NotNull Collection<User> users() {
        return users.values();
    }
}
