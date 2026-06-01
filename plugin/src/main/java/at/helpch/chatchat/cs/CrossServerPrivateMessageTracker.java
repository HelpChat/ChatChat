package at.helpch.chatchat.cs;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.cache.RemoteReplyCache;
import at.helpch.chatchat.util.Constants;
import at.helpch.chatchat.util.MessageUtils;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks pending cross-server private messages until a remote server acknowledges them.
 */
public final class CrossServerPrivateMessageTracker {

    private static final Map<UUID, PendingMessage> PENDING_MESSAGES = new ConcurrentHashMap<>();

    private CrossServerPrivateMessageTracker() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static void registerPending(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final UUID requestId,
        @NotNull final UUID senderUuid,
        @NotNull final String recipientName,
        @NotNull final String senderMessage,
        @NotNull final String socialSpyMessage
    ) {
        final BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(
            plugin,
            () -> timeout(plugin, requestId),
            Constants.PRIVATE_MESSAGE_ACK_TIMEOUT_TICKS
        );

        PENDING_MESSAGES.put(
            requestId,
            new PendingMessage(senderUuid, recipientName, senderMessage, socialSpyMessage, timeoutTask)
        );
    }

    public static void cancelPending(@NotNull final UUID requestId) {
        final var pendingMessage = PENDING_MESSAGES.remove(requestId);
        if (pendingMessage != null) {
            pendingMessage.timeoutTask().cancel();
        }
    }

    public static void complete(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final UUID requestId,
        final boolean delivered
    ) {
        final var pendingMessage = PENDING_MESSAGES.remove(requestId);
        if (pendingMessage == null) {
            return;
        }

        pendingMessage.timeoutTask().cancel();

        final var senderUser = plugin.usersHolder().getUser(pendingMessage.senderUuid());
        if (!(senderUser instanceof final ChatUser sender) || sender.player() == null) {
            return;
        }

        if (!delivered) {
            sender.sendMessage(plugin.configManager().messages().cantMessageGeneral());
            return;
        }

        sender.sendMessage(MessageUtils.parseFromGson(pendingMessage.senderMessage()));

        Audience.audience(
            plugin.usersHolder().users()
                .stream()
                .filter(ChatUser.class::isInstance)
                .map(ChatUser.class::cast)
                .filter(ChatUser::socialSpy)
                .filter(spy -> !spy.uuid().equals(sender.uuid()))
                .toList()
        ).sendMessage(MessageUtils.parseFromGson(pendingMessage.socialSpyMessage()));

        RemoteReplyCache.remember(sender.uuid(), pendingMessage.recipientName());
    }

    private static void timeout(@NotNull final ChatChatPlugin plugin, @NotNull final UUID requestId) {
        final var pendingMessage = PENDING_MESSAGES.remove(requestId);
        if (pendingMessage == null) {
            return;
        }

        final var senderUser = plugin.usersHolder().getUser(pendingMessage.senderUuid());
        if (senderUser instanceof final ChatUser sender && sender.player() != null) {
            sender.sendMessage(plugin.configManager().messages().userOffline());
        }
    }

    private record PendingMessage(
        UUID senderUuid,
        String recipientName,
        String senderMessage,
        String socialSpyMessage,
        BukkitTask timeoutTask
    ) {
    }
}

