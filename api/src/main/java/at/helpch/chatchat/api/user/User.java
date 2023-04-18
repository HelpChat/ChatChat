package at.helpch.chatchat.api.user;

import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.format.Format;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identified;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a simple user.
 */
public interface User extends ForwardingAudience.Single, Identified {

    /**
     * Get the channel that the user is in.
     *
     * @return The channel that the user is in.
     */
    @NotNull Channel channel();

    /**
     * Change the user's channel.
     *
     * @param channel The channel to change to.
     */
    void channel(@NotNull final Channel channel);

    /**
     * Get the user's chat format.
     *
     * @return The user's chat format.
     * {@code @WARNING} This is currently not used by ChatChat at all!
     */
    @NotNull Format format();

    /**
     * Change the user's chat format.
     *
     * @param format The new chat format.
     */
    void format(@NotNull final Format format);

    /**
     * Get the user's unique identifier.
     *
     * @return The user's unique identifier.
     */
    @NotNull UUID uuid();

    /**
     * Check if the user has a permission.
     *
     * @param node The permission to check.
     * @return True if the user has the permission, false otherwise.
     */
    boolean hasPermission(@NotNull final String node);

    /**
     * Checks to see if the user can see another {@link User}.
     *
     * @param target The target to check if the user can see.
     * @return True if the user can see the target, false otherwise.
     */
    boolean canSee(@NotNull final User target);

    /**
     * Get a set of users that this user has ignored.
     *
     * @return The ignored users.
     */
    @NotNull Set<UUID> ignoredUsers();

    /**
     * Replace the ignored users with a new set.
     *
     * @param users The new ignored users.
     */
    void ignoredUsers(@NotNull final Set<UUID> users);

    /**
     * Ignore a user.
     *
     * @param user The user to ignore.
     */
    void ignoreUser(@NotNull final User user);

    /**
     * Unignore a user.
     *
     * @param user The user to unignore.
     */
    void unignoreUser(@NotNull final User user);

    /**
     * Checks if the user has their chat enabled or not.
     *
     * @return True if the user has their chat enabled, false otherwise.
     */
    boolean chatEnabled();

    /**
     * Changes the state of the user's chat.
     *
     * @param toggle True to enable chat, false to disable.
     */
    void chatState(boolean toggle);
}
