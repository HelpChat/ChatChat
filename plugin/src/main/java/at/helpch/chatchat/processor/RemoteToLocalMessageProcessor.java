package at.helpch.chatchat.processor;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.user.ConsoleUser;
import at.helpch.chatchat.util.MessageUtils;
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

        // TODO: Trigger an event?

        final var component = MessageUtils.parseFromGson(message);

        for (final var recipient : recipients) {
            // TODO: Figure out how to support cross server console
            if (recipient instanceof ConsoleUser) continue;

            recipient.sendMessage(component);
        }

        return true;
    }
}
