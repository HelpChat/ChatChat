package at.helpch.chatchat.api.channel;

import at.helpch.chatchat.api.holder.FormatsHolder;

import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * This class is used to register new channel types. Channels registered here can be used in channels.yml.
 */
public interface ChannelTypeRegistry {

    /**
     * Represents a builder for a channel type.
     * @param <T> The channel type.
     */
    @FunctionalInterface
    interface Builder<T extends Channel> {
        @NotNull T build(@NotNull final String name,
                         @NotNull final String messagePrefix,
                         @NotNull final List<String> toggleCommands,
                         @NotNull final String channelPrefix,
                         @NotNull final FormatsHolder formats,
                         final int radius,
                         final boolean crossServer);
    }

    /**
     * Registers a new channel type.
     *
     * @param name The name of the channel type.
     * @param builder The builder for the channel type.
     */
    void add(final @NotNull String name, final @NotNull Builder<?> builder);
}
