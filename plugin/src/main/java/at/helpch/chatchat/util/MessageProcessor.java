package at.helpch.chatchat.util;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.event.ChatChatEvent;
import at.helpch.chatchat.api.user.ChatUser;
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
        final var rulesResult = plugin.ruleManager().isAllowedPublicChat(user, message);
        if (rulesResult.isPresent()) {
            user.sendMessage(rulesResult.get());
            return false;
        }

        final var chatEvent = new ChatChatEvent(
            async,
            user,
            FormatUtils.findFormat(user.player(), channel, plugin.configManager().formats()),
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
        final var mentions = plugin.configManager().settings().mentions();

        var userMessage = parsedMessage;
        var userIsTarget = false;

        for (final var target : chatEvent.recipients()) {
            if (target.uuid() == user.uuid()) {
                userIsTarget = true;
                continue;
            }

            // Console Users have their own format we set in ChatListener.java
            if (target instanceof ConsoleUser) continue;

            // Process mentions and get the result.
            final var mentionResult = plugin.mentionsManager().processMentions(
                async,
                user,
                target,
                chatEvent.channel(),
                parsedMessage,
                true
            );

            if (target instanceof ChatUser) {
                final var chatTarget = (ChatUser) target;

                final var component = FormatUtils.parseFormat(
                    chatEvent.format(),
                    user.player(),
                    chatTarget.player(),
                    mentionResult.message()
                );

                target.sendMessage(component);
                if (mentionResult.playSound()) {
                    target.playSound(mentions.sound());
                }
                if (user.canSee(chatTarget)) {
                    userMessage = plugin.mentionsManager().processMentions(
                        async,
                        user,
                        chatTarget,
                        chatEvent.channel(),
                        userMessage,
                        false
                    ).message();
                }
                continue;
            }

            final var component = FormatUtils.parseFormat(
                chatEvent.format(),
                user.player(),
                mentionResult.message()
            );

            target.sendMessage(component);
            if (mentionResult.playSound()) {
                target.playSound(mentions.sound());
            }
        }

        if (!userIsTarget) {
            user.channel(oldChannel);
            return true;
        }

        final var mentionResult = plugin.mentionsManager().processMentions(
            async,
            user,
            user,
            chatEvent.channel(),
            parsedMessage,
            true
        );

        final var component = FormatUtils.parseFormat(
            chatEvent.format(),
            user.player(),
            user.player(),
            mentionResult.message()
        );

        user.sendMessage(component);
        if (mentionResult.playSound()) {
            user.playSound(mentions.sound());
        }

        user.channel(oldChannel);
        return true;
    }

    public static @NotNull Component processMessage(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final ChatUser user,
        @NotNull final String message) {

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

        return !user.player().hasPermission(URL_PERMISSION)
            ? USER_MESSAGE_MINI_MESSAGE.deserialize(message, resolver.build())
            : USER_MESSAGE_MINI_MESSAGE.deserialize(message, resolver.build()).replaceText(URL_REPLACER_CONFIG);
    }

}
