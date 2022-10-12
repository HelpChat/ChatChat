package at.helpch.chatchat.channel;

import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.util.ChannelUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractChannel implements Channel {

    private final String name;

    private final String messagePrefix;

    private final List<String> toggleCommands;

    private final String channelPrefix;

    private final int radius;

    protected AbstractChannel(
            @NotNull final String name,
            @NotNull final String messagePrefix,
            @NotNull final List<String> toggleCommands,
            @NotNull final String channelPrefix,
            final int radius) {
        this.name = name;
        this.messagePrefix = messagePrefix;
        this.toggleCommands = toggleCommands;
        this.channelPrefix = channelPrefix;
        this.radius = radius;
    }

    @Override
    public @NotNull String name() {
        return name;
    }

    @Override
    public @NotNull String messagePrefix() {
        return messagePrefix;
    }

    @Override
    public @NotNull String channelPrefix() {
        return channelPrefix;
    }

    @Override
    public @NotNull List<String> commandNames() {
        return toggleCommands;
    }

    @Override
    public int radius() {
        return radius;
    }

    @Override
    public boolean isUsableBy(@NotNull final ChatUser user) {
        if (ChatChannel.defaultChannel().equals(this)) {
            return true;
        }

        return user.hasPermission(ChannelUtils.USE_CHANNEL_PERMISSION + name());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatChannel that = (ChatChannel) o;
        return messagePrefix.equals(that.messagePrefix()) &&
                toggleCommands.equals(that.commandNames()) &&
                channelPrefix.equals(that.channelPrefix());
    }
}
