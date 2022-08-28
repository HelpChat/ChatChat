package at.helpch.chatchat.channel;

import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.holder.FormatsHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ChannelTypeRegistry {

    @FunctionalInterface
    public interface Builder<T extends Channel> {
        @NotNull T build(@NotNull final String name,
                @NotNull final String messagePrefix,
                @NotNull final List<String> toggleCommands,
                @NotNull final String channelPrefix,
                @NotNull final FormatsHolder formats,
                final int radius);
    }

    private final Map<String, Builder<?>> builders = new HashMap<>();

    {
        add("default", ChatChannel::new);
    }

    public void add(final @NotNull String name, final @NotNull Builder<?> builder) {
        final String lowercase = name.toLowerCase();
        if (builders.containsKey(lowercase)) {
            throw new IllegalStateException("Attempted to register duplicate channel type " + name);
        }
        builders.put(lowercase, builder);
    }

    public @NotNull Map<String, Builder<?>> builders() {
        return Collections.unmodifiableMap(builders);
    }
}

