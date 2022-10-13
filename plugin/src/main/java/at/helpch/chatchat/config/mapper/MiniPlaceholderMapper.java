package at.helpch.chatchat.config.mapper;

import at.helpch.chatchat.placeholder.MiniPlaceholderImpl;
import net.kyori.adventure.text.minimessage.internal.TagInternals;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

public class MiniPlaceholderMapper implements TypeSerializer<MiniPlaceholderImpl> {

    private static final String NAME = "name";
    private static final String REQUIRES_RECIPIENT = "requires-recipient";
    private static final String PARSE_MINI = "parse-mini";
    private static final String PARSE_PAPI = "parse-papi";
    private static final String CLOSING = "closing";
    private static final String MESSAGE = "message";

    private ConfigurationNode nonVirtualNode(final ConfigurationNode source, final Object... path) throws SerializationException {
        if (!source.hasChild(path)) {
            throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node");
        }
        return source.node(path);
    }

    @Override
    public MiniPlaceholderImpl deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final var name = nonVirtualNode(node, NAME).getString();
        if (name == null) {
            throw new SerializationException("Placeholder name missing!");
        }

        try {
            TagInternals.assertValidTagName(name);
        } catch (final IllegalArgumentException ignored) {
            throw new SerializationException("Found invalid placeholder name in placeholders.yml: " + name +
                " - This placeholder will not be loaded.");
        }

        final var requiresRecipient = nonVirtualNode(node, REQUIRES_RECIPIENT).getBoolean(false);
        final var parseMini = nonVirtualNode(node, PARSE_MINI).getBoolean(false);
        final var parsePapi = nonVirtualNode(node, PARSE_PAPI).getBoolean(true);
        final var closing = nonVirtualNode(node, CLOSING).getBoolean(true);
        final var message = nonVirtualNode(node, MESSAGE).getString("");

        return new MiniPlaceholderImpl(name, requiresRecipient, parseMini, parsePapi, closing, message);
    }

    @Override
    public void serialize(Type type, @Nullable MiniPlaceholderImpl placeholder, ConfigurationNode target) throws SerializationException {
        if (placeholder == null) {
            target.raw(null);
            return;
        }

        target.node(NAME).set(placeholder.name());
        target.node(REQUIRES_RECIPIENT).set(placeholder.requiresRecipient());
        target.node(PARSE_MINI).set(placeholder.parseMini());
        target.node(PARSE_PAPI).set(placeholder.parsePapi());
        target.node(CLOSING).set(placeholder.closing());
        target.node(MESSAGE).set(placeholder.message());
    }
}
