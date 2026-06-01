package at.helpch.chatchat.cs.receiver;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.cache.RemoteReplyCache;
import at.helpch.chatchat.cs.CrossServerPrivateMessageTracker;
import at.helpch.chatchat.processor.RemoteToLocalMessageProcessor;
import at.helpch.chatchat.util.Constants;
import at.helpch.chatchat.util.MessageUtils;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import static at.helpch.chatchat.util.Constants.BUNGEE_CROSS_SERVER_CHANNEL;
import static at.helpch.chatchat.util.Constants.CROSS_SERVER_SUB_CHANNEL;

public class BungeeMessageReceiver implements RemoteMessageReceiver {

    private static final long DEDUP_WINDOW_MILLIS = 3_000L;
    private static final int DEDUP_CACHE_MAX_SIZE = 4_096;
    private static final Map<UUID, Long> RECENT_PAYLOADS = new ConcurrentHashMap<>();

    private final @NotNull ChatChatPlugin plugin;

    public BungeeMessageReceiver(final @NotNull ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(final @NotNull String channel, @NotNull final Player player, final byte @NotNull [] message) {
        if (!channel.equals(BUNGEE_CROSS_SERVER_CHANNEL)) return;

        final ByteArrayDataInput in = ByteStreams.newDataInput(message);
        final String subChannel = in.readUTF();

        if (!subChannel.equals(CROSS_SERVER_SUB_CHANNEL)) return;

        final short length = in.readShort();
        final byte[] messageBytes = new byte[length];
        in.readFully(messageBytes);

        if (isDuplicatePayload(messageBytes)) {
            return;
        }

        final DataInputStream messageIn = new DataInputStream(new ByteArrayInputStream(messageBytes));
        try {
            final String messageType = messageIn.readUTF();
            if (!messageType.equals(Constants.PUBLIC_MESSAGE_TYPE)
                && !messageType.equals(Constants.PRIVATE_MESSAGE_TYPE)
                && !messageType.equals(Constants.PRIVATE_MESSAGE_ACK_TYPE)) {
                plugin.getLogger().log(Level.WARNING, "Got cross server message but the message type was invalid: {0}", messageType);
                return;
            }
            if (messageType.equals(Constants.PUBLIC_MESSAGE_TYPE)) {
                final String channelName = messageIn.readUTF();
                final String messageContent = messageIn.readUTF();

                RemoteToLocalMessageProcessor.processRemoteMessageEvent(plugin, channelName, messageContent);
                return;
            }

            if (messageType.equals(Constants.PRIVATE_MESSAGE_ACK_TYPE)) {
                final UUID requestId = UUID.fromString(messageIn.readUTF());
                final boolean delivered = messageIn.readBoolean();
                CrossServerPrivateMessageTracker.complete(plugin, requestId, delivered);
                return;
            }

            final UUID requestId = UUID.fromString(messageIn.readUTF());
            final UUID senderUuid = UUID.fromString(messageIn.readUTF());
            final String senderName = messageIn.readUTF();
            final String recipientName = messageIn.readUTF();
            messageIn.readUTF(); // raw message, currently only used on the origin server
            messageIn.readBoolean(); // reply flag, currently only used on the origin server
            final boolean ignoreBypass = messageIn.readBoolean();
            final String recipientMessage = messageIn.readUTF();
            final String socialSpyMessage = messageIn.readUTF();

            final var result = processPrivateMessage(senderUuid, senderName, recipientName, ignoreBypass, recipientMessage, socialSpyMessage);
            if (result != PrivateMessageResult.NOT_FOUND) {
                plugin.remoteMessageSender().sendPrivateMessageAck(requestId.toString(), result == PrivateMessageResult.DELIVERED);
            }

        } catch (final IOException exception) {
            plugin.getLogger().log(Level.WARNING, "Got cross server message but could not read it.", exception);
        } catch (final IllegalArgumentException exception) {
            plugin.getLogger().log(Level.WARNING, "Got cross server private message with an invalid sender UUID.", exception);
        }
    }

    private static boolean isDuplicatePayload(final byte @NotNull [] payload) {
        final long now = System.currentTimeMillis();
        final UUID payloadId = UUID.nameUUIDFromBytes(payload);

        // Keep this bounded and drop old fingerprints opportunistically.
        if (RECENT_PAYLOADS.size() > DEDUP_CACHE_MAX_SIZE) {
            RECENT_PAYLOADS.entrySet().removeIf(entry -> now - entry.getValue() > DEDUP_WINDOW_MILLIS);
        }

        final Long previousSeenAt = RECENT_PAYLOADS.put(payloadId, now);
        return previousSeenAt != null && now - previousSeenAt <= DEDUP_WINDOW_MILLIS;
    }

    private @NotNull PrivateMessageResult processPrivateMessage(
        @NotNull final UUID senderUuid,
        @NotNull final String senderName,
        @NotNull final String recipientName,
        final boolean ignoreBypass,
        @NotNull final String recipientMessage,
        @NotNull final String socialSpyMessage
    ) {
        final var targetPlayer = findPlayerByName(recipientName);
        if (targetPlayer == null) {
            return PrivateMessageResult.NOT_FOUND;
        }

        final var targetUser = plugin.usersHolder().getUser(targetPlayer.getUniqueId());
        if (!(targetUser instanceof final ChatUser recipient)) {
            return PrivateMessageResult.NOT_FOUND;
        }

        if (!recipient.privateMessages()) {
            return PrivateMessageResult.BLOCKED;
        }

        if (recipient.ignoredUsers().contains(senderUuid) && !ignoreBypass) {
            return PrivateMessageResult.BLOCKED;
        }

        recipient.sendMessage(MessageUtils.parseFromGson(recipientMessage));

        final var spyComponent = MessageUtils.parseFromGson(socialSpyMessage);
        Audience.audience(
            plugin.usersHolder().users()
                .stream()
                .filter(ChatUser.class::isInstance)
                .map(ChatUser.class::cast)
                .filter(ChatUser::socialSpy)
                .filter(user -> !user.uuid().equals(senderUuid) && !user.uuid().equals(recipient.uuid()))
                .toList()
        ).sendMessage(spyComponent);

        if (plugin.configManager().settings().mentions().privateMessage()) {
            recipient.playSound(plugin.configManager().settings().mentions().sound());
        }

        RemoteReplyCache.remember(recipient.uuid(), senderName);
        return PrivateMessageResult.DELIVERED;
    }

    private enum PrivateMessageResult {
        NOT_FOUND,
        BLOCKED,
        DELIVERED
    }

    private @Nullable Player findPlayerByName(@NotNull final String playerName) {
        return plugin.getServer().getOnlinePlayers().stream()
            .filter(target -> target.getName().equalsIgnoreCase(playerName))
            .findFirst()
            .orElse(null);
    }

}
