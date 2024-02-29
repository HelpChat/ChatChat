package at.helpch.chatchat.api.user;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a chat user that is backed by a {@link Player}.
 */
public interface ChatUser extends User {

    /**
     * Gets the player that this user is backed by.
     *
     * @return The player that this user is backed by.
     */
    @NotNull Player player();

    /**
     * Gets the user that this user has last sent a private message to.
     *
     * @return The last messaged user.
     */
    @NotNull Optional<ChatUser> lastMessagedUser();

    /**
     * Change the user that this user has last sent a private message to.
     *
     * @param user The new last messaged user.
     */
    void lastMessagedUser(@Nullable final ChatUser user);

    /**
     * Checks if the user has their private messages enabled.
     *
     * @return True if the user has their private messages enabled, false otherwise.
     */
    boolean privateMessages();

    /**
     * Changes the state of the user's private messages.
     *
     * @param enable True to enable private messages, false to disable.
     */
    void privateMessages(final boolean enable);

    /**
     * Checks if the user has their personal mentions enabled. If they are enabled, other users will be able to directly
     * mention them in chat without the need of the override permission.
     *
     * @return True if the user has their personal mentions enabled, false otherwise.
     */
    boolean personalMentions();

    /**
     * Changes the state of the user's personal mentions.
     *
     * @param receivesPersonalMentions True to enable personal mentions, false to disable.
     */
    void personalMentions(final boolean receivesPersonalMentions);

    /**
     * Checks if the user has their channel mentions enabled. If they are enabled, other users will be able to channel
     * mention them in chat without the need of the override permission.
     *
     * @return True if the user has their channel mentions enabled, false otherwise.
     */
    boolean channelMentions();

    /**
     * Changes the state of the user's channel mentions.
     *
     * @param receivesChannelMentions True to enable channel mentions, false to disable.
     */
    void channelMentions(final boolean receivesChannelMentions);

    /**
     * Checks if the user has social spy enabled. If it is enabled, they will see all the private messages other users
     * are sending.
     *
     * @return True if the user has social spy enabled, false otherwise.
     */
    boolean socialSpy();

    /**
     * Changes the state of the user's social spy.
     *
     * @param enable True to enable social spy, false to disable.
     */
    void socialSpy(final boolean enable);

    /**
     * Checks if the user has ranged chat enabled.
     * If it is enabled, they will only see messages from players within a certain range.
     * Only applies to the players that have bypass ChannelUtils.BYPASS_RADIUS_CHANNEL_PERMISSION.
     *
     * @return True if the user has ranged chat enabled, false otherwise.
     */
    boolean rangedChat();

    /**
     * Changes the state of the user's ranged chat.
     *
     * @param enable True to enable ranged chat, false to disable.
     */
    void rangedChat(final boolean enable);
}
