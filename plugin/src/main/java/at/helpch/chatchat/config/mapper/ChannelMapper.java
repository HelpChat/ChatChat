package at.helpch.chatchat.config.mapper;

import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.towny.TownyNationChannel;
import at.helpch.chatchat.towny.TownyTownChannel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;

public final class ChannelMapper implements TypeSerializer<Channel> {

    private static final String TOGGLE_COMMAND = "toggle-command";
    private static final String MESSAGE_PREFIX = "message-prefix";
    private static final String CHANNEL_PREFIX = "channel-prefix";
    private static final String TYPE = "type";

    private ConfigurationNode nonVirtualNode(final ConfigurationNode source, final Object... path) throws SerializationException {
        if (!source.hasChild(path)) {
            throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node");
        }
        return source.node(path);
    }

    @Override
    public Channel deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final var keyNode = node.key();
        if (keyNode == null) {
            throw new SerializationException("A config key cannot be null!");
        }
        final var key = keyNode.toString();

        final var commandName = nonVirtualNode(node, TOGGLE_COMMAND).getString();
        if (commandName == null) {
            throw new SerializationException("Command name for " + key + " cannot be null!");
        }

        final var messagePrefix = nonVirtualNode(node, MESSAGE_PREFIX).getString("");
        final var channelPrefix = nonVirtualNode(node, CHANNEL_PREFIX).getString("");

        switch (node.node(TYPE).getString("chat")) {
            case "TOWNY_TOWN":
                return new TownyTownChannel(key, messagePrefix, commandName, channelPrefix);
            case "TOWNY_NATION":
                return new TownyNationChannel(key, messagePrefix, commandName, channelPrefix);
            default:
                return new ChatChannel(key, messagePrefix, commandName, channelPrefix);
        }
    }

    @Override
    public void serialize(Type type, @Nullable Channel channel, ConfigurationNode target) throws SerializationException {
        if (channel == null) {
            target.raw(null);
            return;
        }

        target.node(TOGGLE_COMMAND).set(channel.commandName());
        target.node(MESSAGE_PREFIX).set(channel.messagePrefix());
        target.node(CHANNEL_PREFIX).set(channel.channelPrefix());
    }
}
