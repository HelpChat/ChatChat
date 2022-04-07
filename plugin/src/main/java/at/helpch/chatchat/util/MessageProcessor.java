package at.helpch.chatchat.util;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.event.ChatChatEvent;
import java.util.Map;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;

public class MessageProcessor {
    private static final Pattern DEFAULT_URL_PATTERN = Pattern.compile("(?:(https?)://)?([-\\w_.]+\\.\\w{2,})(/\\S*)?");
    private static final Pattern URL_SCHEME_PATTERN = Pattern.compile("^[a-z][a-z0-9+\\-.]*:");

    private static final TextReplacementConfig URL_REPLACER_CONFIG = TextReplacementConfig.builder()
        .match(DEFAULT_URL_PATTERN)
        .replacement(builder -> {
            String clickUrl = builder.content();
            if (!URL_SCHEME_PATTERN.matcher(clickUrl).find()) {
                clickUrl = "https://" + clickUrl;
            }
            return builder.clickEvent(ClickEvent.openUrl(clickUrl));
        })
        .build();

    private static final String URL_PERMISSION = "chatchat.url";
    private static final String UTF_PERMISSION = "chatchat.utf";
    private static final String MENTION_PERMISSION = "chatchat.mention";
    private static final String MENTION_EVERYONE_PERMISSION = "chatchat.mention.everyone";
    private static final String FORMAT_BASE_PERMISSION = "chatchat.tag.";

    private static final Map<String, TagResolver> PERMISSION_TAGS = Map.ofEntries(
        Map.entry("click", StandardTags.clickEvent()),
        Map.entry("color", StandardTags.color()),
        Map.entry("font", StandardTags.font()),
        Map.entry("gradient", StandardTags.gradient()),
        Map.entry("hover", StandardTags.hoverEvent()),
        Map.entry("insertion", StandardTags.insertion()),
        Map.entry("keybind", StandardTags.keybind()),
        Map.entry("newline", StandardTags.newline()),
        Map.entry("rainbow", StandardTags.rainbow()),
        Map.entry("reset", StandardTags.reset()),
        Map.entry("translatable", StandardTags.translatable())
    );

    public static void process(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final ChatUser user,
        @NotNull final Channel channel,
        @NotNull final String message,
        final boolean async
    ) {
        if (StringUtils.containsIllegalChars(message) && !user.player().hasPermission(UTF_PERMISSION)) {
            user.sendMessage(Component.text("You can't use special characters in chat!", NamedTextColor.RED));
            return;
        }

        final var resolver = TagResolver.builder();

        for (final var entry : PERMISSION_TAGS.entrySet()) {
            if (!user.player().hasPermission(FORMAT_BASE_PERMISSION + entry.getKey())) {
                continue;
            }

            resolver.resolver(entry.getValue());
        }

        for (final var tag : TextDecoration.values()) {
            if (!user.player().hasPermission(FORMAT_BASE_PERMISSION + tag.toString())) {
                continue;
            }

            resolver.resolver(StandardTags.decorations(tag));
        }

        final var miniMessage = MiniMessage.builder().tags(resolver.build()).build();
        final var deserializedMessage = !user.player().hasPermission(URL_PERMISSION)
            ? miniMessage.deserialize(message)
            : miniMessage.deserialize(message).replaceText(URL_REPLACER_CONFIG);

        final var format = FormatUtils.findFormat(user.player(), plugin.configManager().formats());

        final var chatEvent = new ChatChatEvent(
            async,
            user,
            format,
            deserializedMessage,
            channel
        );

        plugin.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        final var oldChannel = user.channel();
        user.channel(channel);
        var component = FormatUtils.parseFormat(
            chatEvent.format(),
            user.player(),
            chatEvent.message()
        );

        final var mentionPrefix = plugin.configManager().settings().mentionPrefix();
        final var mentionSound = plugin.configManager().settings().mentionSound();
        final var mentionFormat = plugin.configManager().settings().mentionFormat();
        var mentionEveryone = false;

        if (user.player().hasPermission(MENTION_EVERYONE_PERMISSION)) {
            final var replaced = MentionUtils.replaceMention(mentionPrefix + "(everyone|here|channel)",
                    component, plugin.configManager().settings().globalMentionFormat());
            component = replaced.component();
            mentionEveryone = replaced.didReplace();
        }

        for (final var target : channel.targets(user)) {
            var mention = mentionEveryone;
            var transformedComponent = component;
            if (target instanceof ChatUser && user.player().hasPermission(MENTION_PERMISSION)) {
                final var replaced = MentionUtils.replaceMention((ChatUser) target, mentionPrefix,
                        component, mentionFormat);
                mention = replaced.didReplace() || mention;
                transformedComponent = replaced.component();
            }
            if (mention) target.playSound(mentionSound);
            target.sendMessage(transformedComponent);
        }
        user.channel(oldChannel);
    }
}
