package at.helpch.chatchat.api.channel;

import at.helpch.chatchat.api.holder.FormatsHolder;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface ChannelTypeRegistry {
    @FunctionalInterface
    interface Builder<T extends Channel> {
        @NotNull T build(@NotNull final String name,
                         @NotNull final String messagePrefix,
                         @NotNull final List<String> toggleCommands,
                         @NotNull final String channelPrefix,
                         @NotNull final FormatsHolder formats,
                         final int radius);
    }

    void add(final @NotNull String name, final @NotNull Builder<?> builder);
}
