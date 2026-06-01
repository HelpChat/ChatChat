package at.helpch.chatchat.cs.sender;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.util.Constants;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import static at.helpch.chatchat.util.Constants.BUNGEE_CROSS_SERVER_CHANNEL;
import static at.helpch.chatchat.util.Constants.CROSS_SERVER_SUB_CHANNEL;

public class BungeeMessageSender implements RemoteMessageSender {
    private final @NotNull ChatChatPlugin plugin;

    public BungeeMessageSender(final @NotNull ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean send(String channel, String message) {
        final ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
        final DataOutputStream messageOut = new DataOutputStream(messageBytes);

        try {
            messageOut.writeUTF(Constants.PUBLIC_MESSAGE_TYPE);
            messageOut.writeUTF(channel);
            messageOut.writeUTF(message);
        } catch (final IOException exception){
            plugin.getLogger().log(Level.WARNING, "Could not write cross server message.", exception);
            return false;
        }

        return sendToAllServers(messageBytes.toByteArray());
    }

    @Override
    public boolean sendPrivateMessage(final PrivateMessagePayload payload) {
        final ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
        final DataOutputStream messageOut = new DataOutputStream(messageBytes);

        try {
            messageOut.writeUTF(Constants.PRIVATE_MESSAGE_TYPE);
            messageOut.writeUTF(payload.requestId());
            messageOut.writeUTF(payload.senderUuid());
            messageOut.writeUTF(payload.senderName());
            messageOut.writeUTF(payload.recipientName());
            messageOut.writeUTF(payload.rawMessage());
            messageOut.writeBoolean(payload.reply());
            messageOut.writeBoolean(payload.ignoreBypass());
            messageOut.writeUTF(payload.recipientMessage());
            messageOut.writeUTF(payload.socialSpyMessage());
        } catch (final IOException exception){
            plugin.getLogger().log(Level.WARNING, "Could not write cross server private message.", exception);
            return false;
        }

        return sendToAllServers(messageBytes.toByteArray());
    }

    @Override
    public boolean sendPrivateMessageAck(final String requestId, final boolean delivered) {
        final ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
        final DataOutputStream messageOut = new DataOutputStream(messageBytes);

        try {
            messageOut.writeUTF(Constants.PRIVATE_MESSAGE_ACK_TYPE);
            messageOut.writeUTF(requestId);
            messageOut.writeBoolean(delivered);
        } catch (final IOException exception) {
            plugin.getLogger().log(Level.WARNING, "Could not write cross server private message ACK.", exception);
            return false;
        }

        return sendToAllServers(messageBytes.toByteArray());
    }

    private boolean sendToAllServers(final byte[] payload) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF(CROSS_SERVER_SUB_CHANNEL);

        out.writeShort(payload.length);
        out.write(payload);

        final Player carrier = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (carrier == null) {
            plugin.getLogger().warning("Could not send cross server message because no online player is available as plugin-message carrier.");
            return false;
        }

        carrier.sendPluginMessage(plugin, BUNGEE_CROSS_SERVER_CHANNEL, out.toByteArray());
        return true;
    }
}
