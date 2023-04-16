package at.helpch.chatchat.cs.receiver;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.processor.RemoteToLocalMessageProcessor;
import at.helpch.chatchat.util.Constants;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;

import static at.helpch.chatchat.util.Constants.BUNGEE_CROSS_SERVER_CHANNEL;
import static at.helpch.chatchat.util.Constants.CROSS_SERVER_SUB_CHANNEL;

public class BungeeMessageReceiver implements RemoteMessageReceiver {

    private final @NotNull ChatChatPlugin plugin;

    public BungeeMessageReceiver(final @NotNull ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(final @NotNull String channel, @NotNull Player player, final byte[] message) {
        if (!channel.equals(BUNGEE_CROSS_SERVER_CHANNEL)) return;

        final ByteArrayDataInput in = ByteStreams.newDataInput(message);
        final String subChannel = in.readUTF();

        if (!subChannel.equals(CROSS_SERVER_SUB_CHANNEL)) return;

        final short length = in.readShort();
        final byte[] messageBytes = new byte[length];
        in.readFully(messageBytes);

        final DataInputStream messageIn = new DataInputStream(new ByteArrayInputStream(messageBytes));
        try {
            final String messageType = messageIn.readUTF();
            if (!messageType.equals(Constants.PUBLIC_MESSAGE_TYPE) && !messageType.equals(Constants.PRIVATE_MESSAGE_TYPE)) {
                plugin.getLogger().warning("Got cross server message but the message type was invalid: " + messageType);
                return;
            }

            // TODO: Add private message support

            final String channelName = messageIn.readUTF();
            final String messageContent = messageIn.readUTF();

            RemoteToLocalMessageProcessor.processRemoteMessageEvent(plugin, channelName, messageContent);

        } catch (final IOException exception) {
            plugin.getLogger().log(Level.WARNING, "Got cross server message but could not read it.", exception);
        }
    }

}
