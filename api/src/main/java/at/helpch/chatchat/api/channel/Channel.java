package at.helpch.chatchat.api.channel;

import at.helpch.chatchat.api.holder.FormatsHolder;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * Represents a channel.
 */
public interface Channel {

    /**
     * Get the name of the channel.
     *
     * @return The name of the channel.
     */
    @NotNull String name();

    /**
     * Get the message prefix of the channel. This prefix can be used at the start of a message to automatically send
     * the message in this channel instead of having the {@link User} switch the channel first.
     *
     * @return The message prefix of the channel.
     */
    @NotNull String messagePrefix();

    /**
     * Get the channel prefix of this channel. This is for display purposes.
     *
     * @return The channel prefix of this channel.
     */
    @NotNull String channelPrefix();

    /**
     * Get a list of commands that can be used to switch to this channel.
     *
     * @return The commands that can be used to switch to this channel.
     */
    @NotNull List<String> commandNames();

    /**
     * Get a {@link FormatsHolder} that contains a list of formats that are associated with this channel. The list can
     * be empty, but any format in the list will take precedence over global formats.
     *
     * @return The formats associated with this channel.
     */
    @NotNull FormatsHolder formats();

    /**
     * Get the radius of this channel. Radius can be set to -1 to be disabled.
     *
     * @return The radius of this channel.
     */
    int radius();

    /**
     * Check if this channel is cross server.
     *
     * @return True if this channel is cross server, false otherwise.
     */
    boolean crossServer();

    /**
     * Get a set of {@link ChatUser}s that can see this channel.
     *
     * @param source The {@link User} that is requesting the list.
     * @return The {@link ChatUser}s that can see this channel.
     */
    Set<User> targets(@NotNull final User source);

    /**
     * Get a set of {@link ChatUser}s that can send messages in this channel.
     *
     * @param user The {@link User} that is requesting the list.
     * @return The {@link ChatUser}s that can send messages in this channel.
     */
    boolean isUsableBy(@NotNull final ChatUser user);
}
