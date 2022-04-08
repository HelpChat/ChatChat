package at.helpch.chatchat.config.mapper;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Channel;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A mapper for the channel map, ignoring invalid channels instead of failing entirely.
 */
public class ChannelMapMapper implements TypeSerializer<Map<String, Channel>> {

    private final ChannelMapper channelMapper;

    private final ChatChatPlugin plugin;

    public ChannelMapMapper(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
        this.channelMapper = new ChannelMapper(plugin.channelTypeRegistry());
    }

    @Override
    public Map<String, Channel> deserialize(final Type type, final ConfigurationNode node) {
        final Map<String, Channel> out = new HashMap<>();

        if (!node.isMap()) return out;

        for (Map.Entry<Object, ? extends ConfigurationNode> ent : node.childrenMap().entrySet()) {
            Channel parsed;
            try {
                parsed = channelMapper.deserialize(Channel.class, ent.getValue());
            } catch (SerializationException ex) {
                plugin.getLogger().warning(ex.getMessage());
                continue;
            }
            out.put(ent.getKey().toString(), parsed);
        }
        return out;
    }

    @Override
    public void serialize(final Type type, @Nullable final Map<String, Channel> obj, final ConfigurationNode node) throws SerializationException {
        if (obj == null || obj.isEmpty()) {
            node.set(Collections.emptyMap());
            return;
        }
        obj.forEach((k, v) -> {
            try {
                channelMapper.serialize(Channel.class, v, node.node(k));
            } catch (SerializationException ignored) {}
        });
    }
}
