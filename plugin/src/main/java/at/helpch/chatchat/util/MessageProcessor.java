package at.helpch.chatchat.util;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.User;
import at.helpch.chatchat.api.event.ChatChatEvent;
import at.helpch.chatchat.format.PMFormat;
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

public final class MessageProcessor {
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
    private static final String MENTION_PERSONAL_PERMISSION = "chatchat.mention.personal";
    private static final String MENTION_EVERYONE_PERMISSION = "chatchat.mention.everyone";
    private static final String MENTION_PERSONAL_BLOCK_PERMISSION = MENTION_PERSONAL_PERMISSION + ".block";
    private static final String MENTION_EVERYONE_BLOCK_PERMISSION = MENTION_EVERYONE_PERMISSION + ".block";
    private static final String MENTION_EVERYONE_BLOCK_OVERRIDE_PERMISSION = MENTION_EVERYONE_BLOCK_PERMISSION + ".override";
    private static final String MENTION_PERSONAL_BLOCK_OVERRIDE_PERMISSION = MENTION_PERSONAL_BLOCK_PERMISSION + ".override";
    private static final String TAG_BASE_PERMISSION = "chatchat.tag.";
    private static final String ITEM_TAG_PERMISSION = TAG_BASE_PERMISSION + "item";

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

    private MessageProcessor() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static void process(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final ChatUser user,
        @NotNull final Channel channel,
        @NotNull final String message,
        final boolean async
    ) {
        if (StringUtils.containsIllegalChars(message) && !user.player().hasPermission(UTF_PERMISSION)) {
            user.sendMessage(plugin.configManager().messages().specialCharactersNoPermission());
            return;
        }

        final var resolver = TagResolver.builder();

        for (final var entry : PERMISSION_TAGS.entrySet()) {
            if (!user.player().hasPermission(TAG_BASE_PERMISSION + entry.getKey())) {
                continue;
            }

            resolver.resolver(entry.getValue());
        }

        for (final var tag : TextDecoration.values()) {
            if (!user.player().hasPermission(TAG_BASE_PERMISSION + tag.toString())) {
                continue;
            }

            resolver.resolver(StandardTags.decorations(tag));
        }

        if (user.player().hasPermission(ITEM_TAG_PERMISSION)) {
            resolver.resolver(
                ItemUtils.createItemPlaceholder(
                    plugin.configManager().settings().itemFormat(),
                    plugin.configManager().settings().itemFormatInfo(),
                    user.player().getInventory().getItemInMainHand()
                )
            );
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

        final var parsedMessage = chatEvent.message().compact();

        final var mentionPrefix = plugin.configManager().settings().mentionPrefix();
        final var mentionSound = plugin.configManager().settings().mentionSound();
        final var mentionFormat = plugin.configManager().settings().mentionFormat();
        final var globalMentionFormat = plugin.configManager().settings().globalMentionFormat();

        var userMessage = parsedMessage;
        var userIsTarget = false;

        for (final var target : channel.targets(user)) {
            if (target.uuid() == user.uuid()) {
                userIsTarget = true;
                continue;
            }

            final var globalMentionProcessResult = processGlobalMentions(
                mentionPrefix,
                globalMentionFormat,
                user,
                target,
                parsedMessage
            );

            if (!(target instanceof ChatUser)) {
                if (globalMentionProcessResult.getKey()) target.playSound(mentionSound);
                target.sendMessage(globalMentionProcessResult.getValue());
                continue;
            }

            final var mentionProcessResult = processPersonalMentions(
                mentionPrefix,
                mentionFormat,
                user,
                (ChatUser) target,
                globalMentionProcessResult.getValue()
            );

            var component = FormatUtils.parseFormat(
                chatEvent.format(),
                user.player(),
                mentionProcessResult.getValue()
            );

            if (mentionProcessResult.getKey() || globalMentionProcessResult.getKey()) target.playSound(mentionSound);
            target.sendMessage(component);

            userMessage = processPersonalMentions(
                mentionPrefix,
                mentionFormat,
                user,
                (ChatUser) target,
                userMessage
            ).getValue();
        }

        if (!userIsTarget) {
            user.channel(oldChannel);
            return;
        }

        final var globalMentionProcessResult = processGlobalMentions(
            mentionPrefix,
            globalMentionFormat,
            user,
            user,
            userMessage
        );

        final var mentionProcessResult = processPersonalMentions(
            mentionPrefix,
            mentionFormat,
            user,
            user,
            globalMentionProcessResult.getValue()
        );

        var component = FormatUtils.parseFormat(
            chatEvent.format(),
            user.player(),
            mentionProcessResult.getValue()
        );

        if (mentionProcessResult.getKey() || globalMentionProcessResult.getKey()) user.playSound(mentionSound);
        user.sendMessage(component);
        user.channel(oldChannel);
    }

    private static @NotNull Map.Entry<@NotNull Boolean, @NotNull Component> processGlobalMentions(
        @NotNull final String mentionPrefix,
        @NotNull final String globalMentionFormat,
        @NotNull final ChatUser user,
        @NotNull final User target,
        @NotNull final Component message
    ) {
        if (!user.player().hasPermission(MENTION_EVERYONE_PERMISSION)) {
            return Map.entry(false, message);
        }

        if (target instanceof ChatUser) {
            final var targetChatUser = (ChatUser) target;

            if (targetChatUser.player().hasPermission(MENTION_EVERYONE_BLOCK_PERMISSION) && !user.player().hasPermission(MENTION_EVERYONE_BLOCK_OVERRIDE_PERMISSION)) {
                return Map.entry(false, message);
            }

            final var replaced = MentionUtils.replaceMention(
                mentionPrefix + "(everyone|here|channel)",
                message,
                globalMentionFormat);

            return Map.entry(replaced.didReplace(), replaced.component());
        }

        final var replaced = MentionUtils.replaceMention(
            mentionPrefix + "(everyone|here|channel)",
            message,
            globalMentionFormat);

        return Map.entry(replaced.didReplace(), replaced.component());
    }

    private static @NotNull Map.Entry<@NotNull Boolean, @NotNull Component> processPersonalMentions(
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
