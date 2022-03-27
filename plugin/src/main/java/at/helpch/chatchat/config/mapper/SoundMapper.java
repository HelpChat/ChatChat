package at.helpch.chatchat.config.mapper;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import java.lang.reflect.Type;


public class SoundMapper implements TypeSerializer<Sound> {

    private static final String NAME = "name";
    private static final String VOLUME = "volume";
    private static final String PITCH = "pitch";
    private static final String DEFAULT_NAME = "entity.experience_orb.pickup";

    @Override
    public Sound deserialize(final Type type, @NotNull final ConfigurationNode node) {
        // intellij kept on complaining about this for some reason
        @Subst(DEFAULT_NAME) final var name = node.node(NAME).getString(DEFAULT_NAME);
        return Sound.sound(
                Key.key(name),
                Sound.Source.MASTER,
                node.node(VOLUME).getFloat(1.0f),
                node.node(PITCH).getFloat(1.0f)
                );

    }

    @Override
    public void serialize(final Type type, @Nullable final Sound obj, final ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }
        node.node(NAME).set(obj.name().asString());
        node.node(VOLUME).set(obj.volume());
        node.node(PITCH).set(obj.pitch());
    }
}
