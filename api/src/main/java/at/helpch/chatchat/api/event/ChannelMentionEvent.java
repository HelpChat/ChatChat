package at.helpch.chatchat.api.event;

import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a {@link ChatUser} mentions another {@link ChatUser}.
 */
public class ChannelMentionEvent extends MentionEvent {
    public ChannelMentionEvent(
        final boolean async,
        @NotNull final ChatUser user,
        @NotNull final User target,
        @NotNull final Channel channel,
        final boolean playSound
    ) {
        super(async, user, target, channel, playSound);
    }
}
