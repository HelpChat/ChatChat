package at.helpch.chatchat.config.mapper;

import at.helpch.chatchat.format.ChatFormat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

public final class ChatFormatMapper implements TypeSerializer<ChatFormat> {

    private static final String PRIORITY = "priority";
    private static final String PARTS = "parts";

    private ConfigurationNode nonVirtualNode(final ConfigurationNode source, final Object... path) throws SerializationException {
        if (!source.hasChild(path)) {
            throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node");
        }
        return source.node(path);
    }

    @Override
    public ChatFormat deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final var keyNode = node.key();
        if (keyNode == null) {
            throw new SerializationException("A config key cannot be null!");
        }
        final var key = keyNode.toString();

        final var priority = nonVirtualNode(node, PRIORITY).getInt();

        final var parts = nonVirtualNode(node, PARTS).getList(String.class);
        if (parts == null) {
            throw new SerializationException("Parts list of node: " + key + " cannot be null!");
        }

        return new ChatFormat(key, priority, parts);
    }

    @Override
    public void serialize(Type type, @Nullable ChatFormat format, ConfigurationNode target) throws SerializationException {
        if (format == null) {
            target.raw(null);
            return;
        }

        target.node(PRIORITY).set(format.priority());
        target.node(PARTS).set(format.parts());
    }
}
