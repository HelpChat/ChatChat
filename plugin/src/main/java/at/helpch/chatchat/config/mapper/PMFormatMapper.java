package at.helpch.chatchat.config.mapper;

import at.helpch.chatchat.format.PMFormat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

public final class PMFormatMapper implements TypeSerializer<PMFormat> {

    private static final String PARTS = "parts";

    private ConfigurationNode nonVirtualNode(final ConfigurationNode source, final Object... path) throws SerializationException {
        if (!source.hasChild(path)) {
            throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node");
        }
        return source.node(path);
    }

    @Override
    public PMFormat deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final var keyNode = node.key();
        if (keyNode == null) {
            throw new SerializationException("A config key cannot be null!");
        }
        final var key = keyNode.toString();

        final var parts = nonVirtualNode(node, PARTS).getList(String.class);
        if (parts == null) {
            throw new SerializationException("Parts list of node: " + key + " cannot be null!");
        }

        return new PMFormat(key, parts);
    }

    @Override
    public void serialize(Type type, @Nullable PMFormat format, ConfigurationNode target) throws SerializationException {
        if (format == null) {
            target.raw(null);
            return;
        }

        target.node(PARTS).set(format.parts());
    }
}
