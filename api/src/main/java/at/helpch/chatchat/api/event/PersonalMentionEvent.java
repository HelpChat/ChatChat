package at.helpch.chatchat.api.event;

import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.user.ChatUser;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a {@link ChatUser} mentions another {@link ChatUser}.
 */
public class PersonalMentionEvent extends MentionEvent {

    private final ChatUser target;

    public PersonalMentionEvent(
        final boolean async,
        @NotNull final ChatUser user,
        @NotNull final ChatUser target,
        @NotNull final Channel channel,
        final boolean playSound
    ) {
        super(async, user, target, channel, playSound);
        this.target = target;
    }

    /**
     * Get the chat user that was mentioned.
     *
     * @return The target of the mention.
     */
    @Override
    public @NotNull ChatUser target() {
        return target;
    }
}
