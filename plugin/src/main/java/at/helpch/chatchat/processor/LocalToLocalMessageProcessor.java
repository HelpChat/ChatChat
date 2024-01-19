package at.helpch.chatchat.processor;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.event.ChatChatEvent;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import at.helpch.chatchat.placeholder.MiniPlaceholderContext;
import at.helpch.chatchat.user.ConsoleUser;
import at.helpch.chatchat.util.FormatUtils;
import at.helpch.chatchat.util.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;

public final class LocalToLocalMessageProcessor {

    private LocalToLocalMessageProcessor() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static boolean processLocalMessageEvent(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final ChatChatEvent chatEvent,
        @NotNull final ChatUser sender,
        @NotNull final Channel channel,
        final boolean async
    ) {
        final var oldChannel = sender.channel();
        sender.channel(channel);

        final var parsedMessage = chatEvent.message().compact();
        final var mentions = plugin.configManager().settings().mentions();

        var messageForSender = parsedMessage;
        var senderIsTarget = false;

        for (final var target : chatEvent.recipients()) {
            if (target.uuid() == sender.uuid()) {
                senderIsTarget = true;
                continue;
            }

            // Console Users have their own format we set in ChatListener.java
            if (target instanceof ConsoleUser) continue;

            // Process mentions and get the result.
            final var mentionResult = plugin.mentionsManager().processMentions(
                async,
                sender,
                target,
                chatEvent.channel(),
                parsedMessage,
                true
            );

            if (target instanceof ChatUser) {
                final var chatTarget = (ChatUser) target;

                final var component = FormatUtils.parseFormat(
                    chatEvent.format(),
                    sender.player(),
                    chatTarget.player(),
                    mentionResult.message(),
                    plugin.miniPlaceholdersManager().compileTags(MiniPlaceholderContext.builder().inMessage(false).sender(sender).recipient(target).build())
                );

                target.sendMessage(component);
                if (mentionResult.playSound()) {
                    target.playSound(mentions.sound());
                }
                if (sender.canSee(chatTarget)) {
                    messageForSender = plugin.mentionsManager().processMentions(
                        async,
                        sender,
                        chatTarget,
                        chatEvent.channel(),
                        messageForSender,
                        false
                    ).message();
                }
                continue;
            }

            final var component = FormatUtils.parseFormat(
                chatEvent.format(),
                sender.player(),
                mentionResult.message(),
                plugin.miniPlaceholdersManager().compileTags(MiniPlaceholderContext.builder().inMessage(false).sender(sender).recipient(target).build())
            );

            target.sendMessage(component);
            if (mentionResult.playSound()) {
                target.playSound(mentions.sound());
            }
        }

        if (!senderIsTarget) {
            sender.channel(oldChannel);
            return true;
        }

        final var mentionResult = plugin.mentionsManager().processMentions(
            async,
            sender,
            sender,
            chatEvent.channel(),
            parsedMessage,
            true
        );

        final var component = FormatUtils.parseFormat(
            chatEvent.format(),
            sender.player(),
            sender.player(),
            mentionResult.message(),
            plugin.miniPlaceholdersManager().compileTags(MiniPlaceholderContext.builder().inMessage(false).sender(sender).recipient(sender).build())
        );

        sender.sendMessage(component);
        if (mentionResult.playSound()) {
            sender.playSound(mentions.sound());
        }

        sender.channel(oldChannel);
        return true;
    }

    public static @NotNull Component processMessage(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final ChatUser sender,
        @NotNull final User recipient,
        @NotNull final String message
    ) {
        final var resolver = TagResolver.builder();

        for (final var entry : MessageProcessor.Constants.PERMISSION_TAGS.entrySet()) {
            if (!sender.hasPermission(MessageProcessor.Constants.TAG_BASE_PERMISSION + entry.getKey())) {
                continue;
            }

            resolver.resolver(entry.getValue());
        }

        for (final var tag : TextDecoration.values()) {
            if (!sender.hasPermission(MessageProcessor.Constants.TAG_BASE_PERMISSION + tag.toString())) {
                continue;
            }

            resolver.resolver(StandardTags.decorations(tag));
        }

        if (sender.hasPermission(MessageProcessor.Constants.ITEM_TAG_PERMISSION)) {
            resolver.resolver(
                ItemUtils.createItemPlaceholder(
                    plugin.configManager().settings().itemFormat(),
                    plugin.configManager().settings().itemFormatInfo(),
                    sender.player().getInventory().getItemInMainHand()
                )
            );
        }

        resolver.resolvers(plugin.miniPlaceholdersManager().compileTags(MiniPlaceholderContext.builder().inMessage(true).sender(sender).recipient(recipient).build()));

        return !sender.hasPermission(MessageProcessor.Constants.URL_PERMISSION)
            ? MessageProcessor.Constants.USER_MESSAGE_MINI_MESSAGE.deserialize(message, resolver.build())
            : MessageProcessor.Constants.USER_MESSAGE_MINI_MESSAGE.deserialize(message, resolver.build()).replaceText(MessageProcessor.Constants.URL_REPLACER_CONFIG);
    }

}
