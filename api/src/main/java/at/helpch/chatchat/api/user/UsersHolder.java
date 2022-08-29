package at.helpch.chatchat.api.user;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class holds all the users that are connected to the server.
 */
public interface UsersHolder {
    /**
     * Gets the user with the given UUID. This will first attempt to get the user from the cache, and if it is not found,
     * it will attempt to get the user from the database. If the user is not found in the database, it will create a new
     * user and add it to the cache.
     *
     * @param uuid The UUID of the user.
     * @return The user with the given UUID.
     */
    @NotNull User getUser(@NotNull UUID uuid);
    /**
     * Gets the user backed by the specified {@link CommandSender}. This will first attempt to get the user from the
     * cache, and if it is not found, it will attempt to get the user from the database. If the user is not found in the
     * database, it will create a new user and add it to the cache.
     *
     * @param user The {@link CommandSender} backing the user.
     * @return The user backed by the specified {@link CommandSender}.
     */
    @NotNull User getUser(@NotNull CommandSender user);

    /**
     * Attempts to remove an {@link User} from the cache and save them to the database. This will do nothing if the user
     * is not cached.
     *
     * @param uuid The UUID of the user to remove.
     */
    void removeUser(@NotNull UUID uuid);

    /**
     * Attempts to remove an {@link User} from the cache and save them to the database. This will do nothing if the user
     * is not cached.
     *
     * @param user The {@link CommandSender} backing the user to remove.
     */
    void removeUser(@NotNull Player user);

    /**
     * Gets all the users that are cached.
     *
     * @return A mutable {@link Collection} of the users that are cached.
     */
    @NotNull Collection<User> users();
}
