package at.helpch.chatchat.user;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import at.helpch.chatchat.api.user.UsersHolder;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class UsersHolderImpl implements UsersHolder {

    public static final User CONSOLE = ConsoleUser.INSTANCE;

    private final ChatChatPlugin plugin;

    private @NotNull final Map<UUID, User> users = new ConcurrentHashMap<>();

    public UsersHolderImpl(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
        users.put(CONSOLE.uuid(), CONSOLE);
    }

    public @NotNull User getUser(@NotNull final UUID uuid) {
        return users.computeIfAbsent(uuid, ignored -> plugin.database().loadChatUser(uuid));
    }

    public @NotNull User getUser(@NotNull final CommandSender user) {
        if (user instanceof ConsoleCommandSender) {
            return CONSOLE;
        }

        return getUser(((Player) user).getUniqueId());
    }

    public void removeUser(@NotNull final UUID uuid) {
        final var user = users.remove(uuid);

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
