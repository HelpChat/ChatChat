package at.helpch.chatchat.processor;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.event.ChatChatEvent;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.user.ConsoleUser;
import at.helpch.chatchat.util.FormatUtils;
import at.helpch.chatchat.util.MessageUtils;
import org.jetbrains.annotations.NotNull;

public final class LocalToRemoteMessageProcessor {

    private LocalToRemoteMessageProcessor() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static boolean processLocalMessageEvent(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final ChatChatEvent chatEvent,
        @NotNull final ChatUser sender,
        @NotNull final Channel channel
    ) {
        if (!channel.crossServer()) {
            return false;
        }

        final var parsedMessage = chatEvent.message().compact();

        final var component = FormatUtils.parseFormat(
            chatEvent.format(),
            sender.player(),
            parsedMessage,
            plugin.miniPlaceholdersManager().compileTags(false, sender, ConsoleUser.INSTANCE)
        );

       return plugin.remoteMessageSender().send(channel.name(), MessageUtils.parseToGson(component));
    }
}
