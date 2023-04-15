package at.helpch.chatchat.processor;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.user.ConsoleUser;
import at.helpch.chatchat.util.FormatUtils;
import org.jetbrains.annotations.NotNull;

public final class RemoteToLocalMessageProcessor {

    private RemoteToLocalMessageProcessor() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static boolean processRemoteMessageEvent(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final String channelName,
        @NotNull final String message
    ) {
        final var channel = plugin.configManager().channels().channels().get(channelName);
        if (channel == null) return false;
        if (!channel.crossServer()) return false;

        final var recipients = channel.targets(ConsoleUser.INSTANCE);

        // TODO: Trigger an event. Either a ChatChatEvent or create a new Event. Current issue with using ChatChatEvent
        //  is that it requires a ChatUser which we don't have. We could maybe change this. We still won't have a User
        //  so we'll have to just pass the console user.

        for (final var recipient : recipients) {
            // TODO: Figure out how to support cross server console
            if (recipient instanceof ConsoleUser) continue;

            final var component = recipient instanceof ChatUser
                ? FormatUtils.parseRemoteFormat(message, ((ChatUser) recipient).player())
                : FormatUtils.parseRemoteFormat(message);

            recipient.sendMessage(component);
        }

        return true;
    }
}
