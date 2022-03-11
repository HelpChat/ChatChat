package at.helpch.chatchat.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class UsersHolder {
    private @NotNull final Map<UUID, ChatUser> users = new HashMap<>();

    public @NotNull ChatUser getUser(@NotNull final UUID uuid) {
        return users.getOrDefault(uuid, addUser(uuid));
    }

    public @NotNull ChatUser getUser(@NotNull final Player player) {
        return getUser(player.getUniqueId());
    }

    public void removeUser(@NotNull final UUID uuid) {
        users.remove(uuid);
    }

    public void removeUser(@NotNull final Player player) {
        removeUser(player.getUniqueId());
    }

    public @NotNull ChatUser addUser(@NotNull final UUID uuid) {
        ChatUser user = new ChatUser(uuid);
        users.put(uuid, user);
        return user;
    }

    public @NotNull ChatUser addUser(@NotNull final Player player) {
        return addUser(player.getUniqueId());
    }

    public @NotNull List<ChatUser> users() {
        return List.copyOf(users.values());
    }
}
