package at.helpch.chatchat.util;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.event.ChatChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public class MessageProcessor {
    private static final String UTF_PERMISSION = "chatchat.utf";
    private static final String MENTION_PERMISSION = "chatchat.mention";
    private static final String MENTION_EVERYONE_PERMISSION = "chatchat.mention.everyone";

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

        final var format = FormatUtils.findFormat(user.player(), plugin.configManager().formats());

        final var chatEvent = new ChatChatEvent(
                async,
                user,
                format,
                Component.text(message),
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
            final var replaced = MentionUtils.replaceMention(mentionPrefix + "(everyone|here|channel)", component, mentionFormat);
            component = replaced.component();
            mentionEveryone = replaced.didReplace();
        }

        for (final var target : channel.targets(user)) {
            var mention = mentionEveryone;
            var transformedComponent = component;
            if (target instanceof ChatUser && user.player().hasPermission(MENTION_PERMISSION)) {
                final var replaced = MentionUtils.replaceMention(mentionPrefix + ((ChatUser) target).player().getName(),
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
