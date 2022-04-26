package at.helpch.chatchat.util;

import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.User;
import at.helpch.chatchat.format.PMFormat;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class MentionProcessor {
    private static final String MENTION_PERSONAL_PERMISSION = "chatchat.mention.personal";
    private static final String MENTION_CHANNEL_PERMISSION = "chatchat.mention.channel";
    private static final String MENTION_PERSONAL_BLOCK_PERMISSION = MENTION_PERSONAL_PERMISSION + ".block";
    private static final String MENTION_CHANNEL_BLOCK_PERMISSION = MENTION_CHANNEL_PERMISSION + ".block";
    private static final String MENTION_PERSONAL_BLOCK_OVERRIDE_PERMISSION = MENTION_PERSONAL_BLOCK_PERMISSION + ".override";
    private static final String MENTION_CHANNEL_BLOCK_OVERRIDE_PERMISSION = MENTION_CHANNEL_BLOCK_PERMISSION + ".override";


    private MentionProcessor() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static @NotNull Map.Entry<@NotNull Boolean, @NotNull Component> processChannelMentions(
        @NotNull final String mentionPrefix,
        @NotNull final String channelMentionFormat,
        @NotNull final ChatUser user,
        @NotNull final User target,
        @NotNull final Component message
    ) {
        if (!user.player().hasPermission(MENTION_CHANNEL_PERMISSION)) {
            return Map.entry(false, message);
        }

        if (target instanceof ChatUser) {
            final var targetChatUser = (ChatUser) target;

            if (targetChatUser.player().hasPermission(MENTION_CHANNEL_BLOCK_PERMISSION) && !user.player().hasPermission(MENTION_CHANNEL_BLOCK_OVERRIDE_PERMISSION)) {
                return Map.entry(false, message);
            }

            final var replaced = MentionUtils.replaceMention(
                mentionPrefix + "(everyone|here|channel)",
                message,
                channelMentionFormat);

            return Map.entry(replaced.didReplace(), replaced.component());
        }

        final var replaced = MentionUtils.replaceMention(
            mentionPrefix + "(everyone|here|channel)",
            message,
            channelMentionFormat);

        return Map.entry(replaced.didReplace(), replaced.component());
    }

    public static @NotNull Map.Entry<@NotNull Boolean, @NotNull Component> processPersonalMentions(
        @NotNull final String mentionPrefix,
        @NotNull final PMFormat mentionFormat,
        @NotNull final ChatUser user,
        @NotNull final ChatUser target,
        @NotNull final Component message
    ) {
        if (!user.player().hasPermission(MENTION_PERSONAL_PERMISSION) ||
            (target.player().hasPermission(MENTION_PERSONAL_BLOCK_PERMISSION) &&
                !user.player().hasPermission(MENTION_PERSONAL_BLOCK_OVERRIDE_PERMISSION))
        ) {
            return Map.entry(false, message);
        }

        final var replaced = MentionUtils.replaceMention(
            target,
            mentionPrefix,
            message,
            mentionFormat
        );

        return Map.entry(replaced.didReplace(), replaced.component());
    }
}
