package at.helpch.chatchat.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class UsersHolder {
    private final @NotNull Map<UUID, ChatUser> users = new HashMap<>();

    public @NotNull ChatUser getUser(UUID uuid) {
        return users.getOrDefault(uuid, addUser(uuid));
    }

    public @NotNull ChatUser getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public void removeUser(@NotNull UUID uuid) {
        users.remove(uuid);
    }

    public void removeUser(@NotNull Player player) {
        removeUser(player.getUniqueId());
    }

    public @NotNull ChatUser addUser(@NotNull UUID uuid) {
        ChatUser user = new ChatUser(uuid);
        users.put(uuid, user);
        return user;
    }

    public @NotNull ChatUser addUser(@NotNull Player player) {
        return addUser(player.getUniqueId());
    }
}