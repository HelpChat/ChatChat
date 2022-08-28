package at.helpch.chatchat.config.mapper;

import at.helpch.chatchat.api.format.PriorityFormat;
import at.helpch.chatchat.format.ChatFormat;
import io.leangen.geantyref.TypeToken;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class PriorityFormatMapper implements TypeSerializer<PriorityFormat> {

    private static final TypeToken<Map<String, List<String>>> mapTypeToken = new TypeToken<>() {};
    private static final String PRIORITY = "priority";
    private static final String PARTS = "parts";

    private ConfigurationNode nonVirtualNode(final ConfigurationNode source, final Object... path) throws SerializationException {
        if (!source.hasChild(path)) {
            throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node");
        }
        return source.node(path);
    }

    @Override
    public PriorityFormat deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final var keyNode = node.key();
        if (keyNode == null) {
            throw new SerializationException("A config key cannot be null!");
        }
        final var key = keyNode.toString();

        final var priority = nonVirtualNode(node, PRIORITY).getInt();

        final var parts = nonVirtualNode(node, PARTS).get(mapTypeToken);
        if (parts == null) {
            throw new SerializationException("Parts list of node: " + key + " cannot be null!");
        }

        return new ChatFormat(key, priority, parts);
    }

    @Override
    public void serialize(Type type, @Nullable PriorityFormat format, ConfigurationNode target) throws SerializationException {
        if (format == null) {
            target.raw(null);
            return;
        }

        target.node(PRIORITY).set(format.priority());
        target.node(PARTS).set(format.parts());
    }
}
