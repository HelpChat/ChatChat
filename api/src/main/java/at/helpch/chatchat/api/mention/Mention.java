package at.helpch.chatchat.api.mention;

import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.user.User;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Mention {
    /**
     * Process the message and replace all mentions of the target by the sender here. The returned mention result should
     * contain the processed message, a boolean that represents if the target was mentioned at all or not and a boolean
     * that represents if the target should hear a sound when they'll receive the message.
     *
     * @param async true if call was done async, false otherwise
     * @param sender the sender of the message
     * @param target the target of the message
     * @param channel the channel the message was sent in
     * @param message the message to process mentions in
     * @param data additional data that can be used to process the message
     * @return a mention result
     */
    @NotNull MentionResult processMention(
        boolean async,
        @NotNull User sender,
        @NotNull User target,
        @NotNull Channel channel,
        @NotNull Component message,
        @Nullable Object data
    );
}
