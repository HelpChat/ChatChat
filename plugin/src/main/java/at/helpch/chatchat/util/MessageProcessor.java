package at.helpch.chatchat.util;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.MentionType;
import at.helpch.chatchat.api.event.ChatChatEvent;
import at.helpch.chatchat.api.event.MentionEvent;
import at.helpch.chatchat.user.ConsoleUser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Pattern;

public final class MessageProcessor {

    private static final MiniMessage USER_MESSAGE_MINI_MESSAGE = MiniMessage.builder().tags(TagResolver.empty()).build();
    private static final Pattern DEFAULT_URL_PATTERN = Pattern.compile("(?:(https?)://)?([-\\w_.]+\\.\\w{2,})(/\\S*)?");
    private static final Pattern URL_SCHEME_PATTERN = Pattern.compile("^[a-z][a-z\\d+\\-.]*:");

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

    public static boolean process(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final ChatUser user,
        @NotNull final Channel channel,
        @NotNull final String message,
        final boolean async
    ) {
        if (StringUtils.containsIllegalChars(message) && !user.hasPermission(UTF_PERMISSION)) {
            user.sendMessage(plugin.configManager().messages().specialCharactersNoPermission());
            return false;
        }

        final var chatEvent = new ChatChatEvent(
            async,
            user,
            FormatUtils.findFormat(user.player(), plugin.configManager().formats()),
            MessageProcessor.processMessage(plugin, user, message),
            channel,
            channel.targets(user)
        );

        plugin.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return false;
        }

        final var oldChannel = user.channel();
        user.channel(channel);

        final var parsedMessage = chatEvent.message().compact();

        final var mentionPrefix = plugin.configManager().settings().mentionPrefix();
        final var mentionSound = plugin.configManager().settings().mentionSound();
        final var personalMentionFormat = plugin.configManager().settings().mentionFormat();
        final var channelMentionFormat = plugin.configManager().settings().channelMentionFormat();

        var userMessage = parsedMessage;
        var userIsTarget = false;

        for (final var target : chatEvent.recipients()) {
            if (target.uuid() == user.uuid()) {
                userIsTarget = true;
                continue;
            }

            if (target instanceof ConsoleUser) continue;

            final var channelMentionProcessResult = MentionUtils.processChannelMentions(
                mentionPrefix,
                channelMentionFormat,
                user,
                target,
                parsedMessage
            );

            final var channelMentionEvent = new MentionEvent(
                async,
                user,
                target,
                chatEvent.channel(),
                MentionType.CHANNEL
            );

            if (channelMentionProcessResult.getKey()) {
                plugin.getServer().getPluginManager().callEvent(channelMentionEvent);
            }

            // Personal mentions can only be used towards ChatUsers.
            if (!(target instanceof ChatUser)) {
                if (!channelMentionProcessResult.getKey() || channelMentionEvent.isCancelled()) {
                    final var component = FormatUtils.parseFormat(
                        chatEvent.format(),
                        user.player(),
                        parsedMessage
                    );

                    target.sendMessage(component);
                    continue;
                }

                final var component = FormatUtils.parseFormat(
                    chatEvent.format(),
                    user.player(),
                    channelMentionProcessResult.getValue()
                );

                target.sendMessage(component);
                if (user.canSee(target)) {
                    target.playSound(mentionSound);
                }
                continue;
            }

            final var chatUserTarget = (ChatUser) target;

            final var personalMentionProcessResult = MentionUtils.processPersonalMentions(
                mentionPrefix,
                personalMentionFormat,
                user,
                chatUserTarget,
                !channelMentionProcessResult.getKey() || channelMentionEvent.isCancelled()
                    ? parsedMessage
                    : channelMentionProcessResult.getValue()
            );

            final var personalMentionEvent = new MentionEvent(
                async,
                user,
                target,
                chatEvent.channel(),
                MentionType.PERSONAL
            );

            if (personalMentionProcessResult.getKey()) {
                plugin.getServer().getPluginManager().callEvent(personalMentionEvent);
            }

            if (!personalMentionProcessResult.getKey() || personalMentionEvent.isCancelled()) {
                if (!channelMentionProcessResult.getKey() || channelMentionEvent.isCancelled()) {
                    final var component = FormatUtils.parseFormat(
                        chatEvent.format(),
                        user.player(),
                        chatUserTarget.player(),
                        parsedMessage
                    );

                    target.sendMessage(component);
                    continue;
                }

                final var component = FormatUtils.parseFormat(
                    chatEvent.format(),
                    user.player(),
                    chatUserTarget.player(),
                    channelMentionProcessResult.getValue()
                );

                target.sendMessage(component);
                if (user.canSee(target)) {
                    target.playSound(mentionSound);
                }
                continue;
            }

            final var component = FormatUtils.parseFormat(
                chatEvent.format(),
                user.player(),
                chatUserTarget.player(),
                personalMentionProcessResult.getValue()
            );

            target.sendMessage(component);
            if (user.canSee(target)) {
                target.playSound(mentionSound);
                userMessage = MentionUtils.processPersonalMentions(
                    mentionPrefix,
                    personalMentionFormat,
                    user,
                    chatUserTarget,
                    userMessage
                ).getValue();
            }
        }

        if (!userIsTarget) {
            user.channel(oldChannel);
            return true;
        }

        final var channelMentionProcessResult = MentionUtils.processChannelMentions(
            mentionPrefix,
            channelMentionFormat,
            user,
            user,
            userMessage
        );

        final var channelMentionEvent = new MentionEvent(
            async,
            user,
            user,
            chatEvent.channel(),
            MentionType.CHANNEL
        );

        if (channelMentionProcessResult.getKey()) {
            plugin.getServer().getPluginManager().callEvent(channelMentionEvent);
        }

        final var personalMentionProcessResult = MentionUtils.processPersonalMentions(
            mentionPrefix,
            personalMentionFormat,
            user,
            user,
            !channelMentionProcessResult.getKey() || channelMentionEvent.isCancelled()
                ? userMessage
                : channelMentionProcessResult.getValue()
        );

        final var personalMentionEvent = new MentionEvent(
            async,
            user,
            user,
            chatEvent.channel(),
            MentionType.PERSONAL
        );

        if (personalMentionProcessResult.getKey()) {
            plugin.getServer().getPluginManager().callEvent(personalMentionEvent);
        }

        if (!personalMentionProcessResult.getKey() || personalMentionEvent.isCancelled()) {
            if (!channelMentionProcessResult.getKey() || channelMentionEvent.isCancelled()) {
                final var component = FormatUtils.parseFormat(
                    chatEvent.format(),
                    user.player(),
                    user.player(),
                    userMessage
                );

                user.sendMessage(component);
                user.channel(oldChannel);
                return true;
            }

            final var component = FormatUtils.parseFormat(
                chatEvent.format(),
                user.player(),
                user.player(),
                channelMentionProcessResult.getValue()
            );

            user.playSound(mentionSound);
            user.sendMessage(component);
            user.channel(oldChannel);
            return true;
        }

        final var component = FormatUtils.parseFormat(
            chatEvent.format(),
            user.player(),
            user.player(),
            personalMentionProcessResult.getValue()
        );

        user.playSound(mentionSound);
        user.sendMessage(component);
        user.channel(oldChannel);
        return true;
    }

    public static @NotNull Component processMessage(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final ChatUser user,
        @NotNull final String message) {

        final var resolver = TagResolver.builder();

        for (final var entry : PERMISSION_TAGS.entrySet()) {
            if (!user.hasPermission(TAG_BASE_PERMISSION + entry.getKey())) {
                continue;
            }

            resolver.resolver(entry.getValue());
        }

        for (final var tag : TextDecoration.values()) {
            if (!user.hasPermission(TAG_BASE_PERMISSION + tag.toString())) {
                continue;
            }

            resolver.resolver(StandardTags.decorations(tag));
        }

        if (user.hasPermission(ITEM_TAG_PERMISSION)) {
            resolver.resolver(
                ItemUtils.createItemPlaceholder(
                    plugin.configManager().settings().itemFormat(),
                    plugin.configManager().settings().itemFormatInfo(),
                    user.player().getInventory().getItemInMainHand()
                )
            );
        }

        return !user.hasPermission(URL_PERMISSION)
            ? USER_MESSAGE_MINI_MESSAGE.deserialize(message, resolver.build())
            : USER_MESSAGE_MINI_MESSAGE.deserialize(message, resolver.build()).replaceText(URL_REPLACER_CONFIG);
    }

}
