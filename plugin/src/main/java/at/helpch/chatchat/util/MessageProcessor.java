package at.helpch.chatchat.util;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.event.ChatChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.atomic.AtomicBoolean;

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

        final AtomicBoolean pingEveryone = new AtomicBoolean(false);
        final var mentionPrefix = plugin.configManager().settings().mentionPrefix();

        // FIXME - tidy up
        if (user.player().hasPermission(MENTION_EVERYONE_PERMISSION)) {
            chatEvent.message(chatEvent.message().replaceText(builder -> builder
                    .match(mentionPrefix + "(here|everyone)")
                    .replacement((result, ignored) -> {
                                pingEveryone.set(true);
                                return Component.text(result.group()).color(NamedTextColor.YELLOW);
                            }
                    )));
        }

        final var oldChannel = user.channel();
        user.channel(channel);
        final var component = FormatUtils.parseFormat(
                chatEvent.format(),
                user.player(),
                chatEvent.message()
        );

        // FIXME - this is messy. make it not
        final var canMention = user.player().hasPermission(MENTION_PERMISSION);
        for (final var target : chatEvent.channel().targets(chatEvent.user())) {
            var userSpecificComponent = component;
            if (canMention && target instanceof ChatUser) {
                final var name = ((ChatUser) target).player().getName();
                userSpecificComponent = component.replaceText(builder ->
                        builder.matchLiteral(mentionPrefix + name)
                                .replacement((result, ignored) -> {
                                    target.playSound(plugin.configManager().settings().mentionSound());
                                    return Component.text(result.group()).color(NamedTextColor.YELLOW);
                                })
                );
            }
            target.sendMessage(user, userSpecificComponent);
            if (pingEveryone.get()) {
                target.playSound(plugin.configManager().settings().mentionSound());
            }
            user.channel(oldChannel);
        }
    }
}
