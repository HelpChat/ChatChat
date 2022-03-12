package at.helpch.chatchat.util;

import at.helpch.chatchat.channel.ChatChannel;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ChannelUtils {

    public static final String BASE_CHANNEL_PERMISSION = "chatchat.channel.";

    private ChannelUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static @NotNull ChatChannel findDefaultChannel(
            @NotNull final Map<String, ChatChannel> channels,
            @NotNull final String defaultChannel) {
        final var channel = channels.get(defaultChannel);
        return Objects.requireNonNullElseGet(channel, ChatChannel::new);
    }

    public static @NotNull Optional<ChatChannel> findChannelByPrefix(
            @NotNull final List<ChatChannel> channels,
            @NotNull final String input) {
        return channels.stream()
                .filter(channel -> !channel.messagePrefix().isEmpty()) // ignore empty prefixes
                .filter(channel -> input.startsWith(channel.messagePrefix()))
                .findFirst();
    }
}
