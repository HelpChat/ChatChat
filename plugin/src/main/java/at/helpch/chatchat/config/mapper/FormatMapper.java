package at.helpch.chatchat.config.mapper;

import at.helpch.chatchat.api.Format;
import at.helpch.chatchat.format.BasicFormat;
import io.leangen.geantyref.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public final class FormatMapper implements TypeSerializer<Format> {

    private static final TypeToken<Map<String, List<String>>> mapTypeToken = new TypeToken<>() {};
    private static final String PARTS = "parts";

    private ConfigurationNode nonVirtualNode(final ConfigurationNode source, final Object... path) throws SerializationException {
        if (!source.hasChild(path)) {
            throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node");
        }
        return source.node(path);
    }

    @Override
    public Format deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final var keyNode = node.key();
        if (keyNode == null) {
            throw new SerializationException("A config key cannot be null!");
        }
        final var key = keyNode.toString();

        final var parts = nonVirtualNode(node, PARTS).get(mapTypeToken);
        if (parts == null) {
            throw new SerializationException("Parts list of node: " + key + " cannot be null!");
        }

        return new BasicFormat(key, parts);
    }

    @Override
    public void serialize(Type type, @Nullable Format format, ConfigurationNode target) throws SerializationException {
        if (format == null) {
            target.raw(null);
            return;
        }

        target.node(PARTS).set(format.parts());
    }
}
