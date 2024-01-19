package at.helpch.chatchat.cs.sender;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.util.Constants;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
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
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF(CROSS_SERVER_SUB_CHANNEL);

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

        out.writeShort(messageBytes.toByteArray().length);
        out.write(messageBytes.toByteArray());

        Bukkit.getServer().sendPluginMessage(plugin, BUNGEE_CROSS_SERVER_CHANNEL, out.toByteArray());
        return true;
    }
}
