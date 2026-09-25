package at.helpch.chatchat.util;

import at.helpch.chatchat.api.format.Format;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public final class MentionUtils {

    private static final String MENTION_PERSONAL_PERMISSION = "chatchat.mention.personal";
    private static final String MENTION_CHANNEL_PERMISSION = "chatchat.mention.everyone";
    public static final String MENTION_PERSONAL_BLOCK_PERMISSION = MENTION_PERSONAL_PERMISSION + ".block";
    public static final String MENTION_CHANNEL_BLOCK_PERMISSION = MENTION_CHANNEL_PERMISSION + ".block";
    private static final String MENTION_PERSONAL_BLOCK_OVERRIDE_PERMISSION = MENTION_PERSONAL_BLOCK_PERMISSION +
        ".override";
    private static final String MENTION_CHANNEL_BLOCK_OVERRIDE_PERMISSION = MENTION_CHANNEL_BLOCK_PERMISSION +
        ".override";
    private static final String MENTION_START = "(?<![A-Za-z0-9_])";
    private static final String MENTION_END = "(?![A-Za-z0-9_])";

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

    static MentionReplaceResult replaceMention(
        @NotNull @RegExp final String username,
        @NotNull final Component component,
        @NotNull final Function<MatchResult, Component> then
    ) {
        final var parts = new ArrayList<Component>();
        collectParts(component, Style.empty(), parts);

        final var pattern = Pattern.compile(username, Pattern.CASE_INSENSITIVE);
        final var output = Component.text();
        final var textRun = new ArrayList<TextComponent>();
        var replaced = false;

        for (final var part : parts) {
            if (part instanceof TextComponent && !MentionProtection.isProtected(part)) {
                textRun.add((TextComponent) part);
                continue;
            }

            replaced |= appendTextRun(output, textRun, pattern, then);
            textRun.clear();
            output.append(part);
        }

        replaced |= appendTextRun(output, textRun, pattern, then);
        return new MentionReplaceResult(replaced, replaced ? output.build() : component);
    }

    private static void collectParts(
        @NotNull final Component component,
        @NotNull final Style inheritedStyle,
        @NotNull final List<Component> parts
    ) {
        final var style = component.style().merge(inheritedStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET, Style.Merge.all());
        if (MentionProtection.isProtected(component) || !(component instanceof TextComponent)) {
            parts.add(component.style(style));
            return;
        }

        final var text = (TextComponent) component;
        if (!text.content().isEmpty()) {
            parts.add(Component.text(text.content()).style(style));
        }

        for (final var child : component.children()) {
            collectParts(child, style, parts);
        }
    }

    private static boolean appendTextRun(
        @NotNull final TextComponent.Builder output,
        @NotNull final List<TextComponent> parts,
        @NotNull final Pattern pattern,
        @NotNull final Function<MatchResult, Component> then
    ) {
        final var text = new StringBuilder();
        for (final var part : parts) {
            text.append(part.content());
        }

        final var matcher = pattern.matcher(text);
        var end = 0;
        var replaced = false;
        while (matcher.find()) {
            appendTextSlice(output, parts, end, matcher.start());
            output.append(then.apply(matcher.toMatchResult()));
            end = matcher.end();
            replaced = true;
        }

        appendTextSlice(output, parts, end, text.length());
        return replaced;
    }

    private static void appendTextSlice(
        @NotNull final TextComponent.Builder output,
        @NotNull final List<TextComponent> parts,
        final int start,
        final int end
    ) {
        var offset = 0;
        for (final var part : parts) {
            final var partEnd = offset + part.content().length();
            final var sliceStart = Math.max(start, offset);
            final var sliceEnd = Math.min(end, partEnd);
            if (sliceStart < sliceEnd) {
                output.append(part.content(part.content().substring(sliceStart - offset, sliceEnd - offset)));
            }
            offset = partEnd;
        }
    }

    public static MentionReplaceResult replaceMention(
        @RegExp @NotNull final String username,
        @NotNull final User user,
        @NotNull final Component component,
        @NotNull final Format format) {
        return replaceMention(username, component, result -> {
            final var mention = Placeholder.component("mention", Component.text(result.group()));
            if (user instanceof ChatUser) {
                final var player = ((ChatUser) user).player();
                if (player.isPresent()) {
                    return FormatUtils.parseFormat(format, player.get(), component, mention);
                }
            }
            return FormatUtils.parseFormat(format, component, mention);
        });
    }

    public static MentionReplaceResult replaceMention(
        @NotNull final ChatUser user,
        @NotNull final String prefix,
        @NotNull final Component component,
        @NotNull final Format format
    ) {
        return user.player()
            .map(value -> replaceMention(
                MENTION_START + Pattern.quote(prefix + value.getName()) + MENTION_END,
                component,
                result -> FormatUtils.parseFormat(
                    format,
                    value,
                    component,
                    Placeholder.component("mention", Component.text(result.group()))
                )
            ))
            .orElseGet(() -> new MentionReplaceResult(false, component));
    }

    public static @NotNull Map.Entry<@NotNull Boolean, @NotNull Component> processChannelMentions(
        @NotNull final String mentionPrefix,
        @NotNull final Format channelMentionFormat,
        @NotNull final ChatUser user,
        @NotNull final User target,
        @NotNull final Component message
    ) {
        if (!user.hasPermission(MENTION_CHANNEL_PERMISSION)) {
            return Map.entry(false, message);
        }

        if (target instanceof ChatUser) {
            final var targetChatUser = (ChatUser) target;

            if (!targetChatUser.channelMentions() && !user.hasPermission(MENTION_CHANNEL_BLOCK_OVERRIDE_PERMISSION)) {
                return Map.entry(false, message);
            }
        }

        final var replaced = MentionUtils.replaceMention(
            MENTION_START + Pattern.quote(mentionPrefix) + "(?:everyone|here|channel)" + MENTION_END,
            target,
            message,
            channelMentionFormat);

        return Map.entry(replaced.didReplace(), replaced.component());
    }

    public static @NotNull Map.Entry<@NotNull Boolean, @NotNull Component> processPersonalMentions(
        @NotNull final String mentionPrefix,
        @NotNull final Format mentionFormat,
        @NotNull final ChatUser user,
        @NotNull final ChatUser target,
        @NotNull final Component message
    ) {
        if (!user.hasPermission(MENTION_PERSONAL_PERMISSION) ||
            (!target.personalMentions() && !user.hasPermission(MENTION_PERSONAL_BLOCK_OVERRIDE_PERMISSION))
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
