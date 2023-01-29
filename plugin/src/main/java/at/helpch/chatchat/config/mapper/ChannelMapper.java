package at.helpch.chatchat.config.mapper;

import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.format.PriorityFormat;
import at.helpch.chatchat.channel.ChannelTypeRegistryImpl;
import at.helpch.chatchat.config.holder.FormatsHolderImpl;
import io.leangen.geantyref.TypeToken;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

public final class ChannelMapper implements TypeSerializer<Channel> {

    private static final String TOGGLE_COMMAND = "toggle-command";
    private static final String MESSAGE_PREFIX = "message-prefix";
    private static final String CHANNEL_PREFIX = "channel-prefix";
    private static final String FORMATS = "formats";
    private static final String RADIUS = "radius";
    private static final String TYPE = "type";
    private static final TypeToken<Map<String, PriorityFormat>> FORMATS_MAP_TYPE = new TypeToken<>() {};

    private final ChannelTypeRegistryImpl registry;

    public ChannelMapper(final ChannelTypeRegistryImpl registry) {
        this.registry = registry;
    }

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

        final var commandName = nonVirtualNode(node, TOGGLE_COMMAND).getList(String.class);
        if (commandName == null) {
            throw new SerializationException("Command name for " + key + " cannot be null!");
        }

        final var messagePrefix = node.node(MESSAGE_PREFIX).getString("");
        final var channelPrefix = node.node(CHANNEL_PREFIX).getString("");
        final var formatsMap = node.node(FORMATS).get(FORMATS_MAP_TYPE, Map.of());
        final var formats = new FormatsHolderImpl(formatsMap);
        final var radius = node.node(RADIUS).getInt(-1);

        final var channelType = node.node(TYPE).getString("default").toLowerCase();

        final var builder = registry.builders().get(channelType);
        if (builder == null) {
            throw new SerializationException("Channel " + key + " has unknown channel type " + channelType + ", " +
                    "ignoring.");
        }
        return builder.build(key, messagePrefix, commandName, channelPrefix, formats, radius);
    }

    @Override
    public void serialize(Type type, @Nullable Channel channel, ConfigurationNode target) throws SerializationException {
        if (channel == null) {
            target.raw(null);
            return;
        }

        target.node(TOGGLE_COMMAND).set(channel.commandNames());
        target.node(MESSAGE_PREFIX).set(channel.messagePrefix());
        target.node(CHANNEL_PREFIX).set(channel.channelPrefix());
        target.node(FORMATS).set(channel.formats().formats());
        target.node(RADIUS).set(channel.radius());
    }
}
