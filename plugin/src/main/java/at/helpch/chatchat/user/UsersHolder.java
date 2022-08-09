package at.helpch.chatchat.user;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
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

    public static final User CONSOLE = ConsoleUser.INSTANCE;

    private final ChatChatPlugin plugin;

    private @NotNull final Map<UUID, User> users = new HashMap<>();

    public UsersHolder(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
        users.put(CONSOLE.uuid(), CONSOLE);
    }

    public @NotNull User getUser(@NotNull final UUID uuid) {
        return users.computeIfAbsent(uuid, user -> plugin.database().loadChatUser(uuid));
    }

    public @NotNull User getUser(@NotNull final CommandSender user) {
        if (user instanceof ConsoleCommandSender) {
            return CONSOLE;
        }

        return getUser(((Player) user).getUniqueId());
    }

    public void removeUser(@NotNull final UUID uuid) {
        if (!users.containsKey(uuid)) {
            return;
        }

        final var user = users.get(uuid);
        users.remove(uuid);

        if (!(user instanceof ChatUser)) {
            return;
        }

        plugin.database().saveChatUser((ChatUser) user);
    }

    public void removeUser(@NotNull final Player player) {
        removeUser(player.getUniqueId());
    }

    public @NotNull Collection<User> users() {
        return users.values();
    }
}
