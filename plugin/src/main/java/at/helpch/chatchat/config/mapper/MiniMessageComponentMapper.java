package at.helpch.chatchat.config.mapper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class MiniMessageComponentMapper implements TypeSerializer<Component> {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public Component deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final var key = node.key();
        if (key == null) {
            throw new SerializationException("A config key cannot be null! " + node.path());
        }

        final var value = node.getString();
        if (value == null) {
            throw new SerializationException("No value was given to " + key);
        }

        return miniMessage.deserialize(value);
    }

    @Override
    public void serialize(Type type, @Nullable Component component, ConfigurationNode target) throws SerializationException {
        if (component == null) {
            target.raw(null);
            return;
        }

        target.set(miniMessage.serialize(component));
    }
}
