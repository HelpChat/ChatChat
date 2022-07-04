package at.helpch.chatchat.util;

import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.Format;
import at.helpch.chatchat.api.User;
import at.helpch.chatchat.format.PMFormat;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public final class MentionUtils {
    private static final String MENTION_PERSONAL_PERMISSION = "chatchat.mention.personal";
    private static final String MENTION_CHANNEL_PERMISSION = "chatchat.mention.channel";
    private static final String MENTION_PERSONAL_BLOCK_PERMISSION = MENTION_PERSONAL_PERMISSION + ".block";
    private static final String MENTION_CHANNEL_BLOCK_PERMISSION = MENTION_CHANNEL_PERMISSION + ".block";
    private static final String MENTION_PERSONAL_BLOCK_OVERRIDE_PERMISSION = MENTION_PERSONAL_BLOCK_PERMISSION + ".override";
    private static final String MENTION_CHANNEL_BLOCK_OVERRIDE_PERMISSION = MENTION_CHANNEL_BLOCK_PERMISSION + ".override";

    private MentionUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static final class MentionReplaceResult {
        private final boolean didReplace;
        private final Component component;

        public MentionReplaceResult(final boolean didReplace, final Component component) {
            this.didReplace = didReplace;
            this.component = component;
        }

        public boolean didReplace() {
            return didReplace;
        }

        public Component component() {
            return component;
        }
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    private static MentionReplaceResult replaceMention(
            @RegExp @NotNull final String username,
            @NotNull final Component component,
            @NotNull final Function<MatchResult, Component> then) {
        final AtomicBoolean hasBeenReplaced = new AtomicBoolean();
        final var replaced = component.replaceText(builder -> builder
                .match(Pattern.compile(username, Pattern.CASE_INSENSITIVE))
                .replacement((result, ignored) -> {
                            hasBeenReplaced.set(true);
                            return then.apply(result);
                        }
                ));
        return new MentionReplaceResult(hasBeenReplaced.get(), replaced);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static MentionReplaceResult replaceMention(
            @RegExp @NotNull final String username,
            @NotNull final Component component,
            @NotNull final String format) {
        return replaceMention(username, component, (r) -> MessageUtils.parseToMiniMessage(format + r.group()));
    }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    public static MentionReplaceResult replaceMention(
            @NotNull final ChatUser user,
            @NotNull final String prefix,
            @NotNull final Component component,
            @NotNull final Format format
            ) {
        return replaceMention(prefix + user.player().getName(), component,
                r -> FormatUtils.parseFormat(format, user.player(), component));
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
