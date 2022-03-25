package at.helpch.chatchat.channel;

import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.util.ChannelUtils;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractChannel implements Channel {

    private final String name;

    private final String messagePrefix;

    private final String toggleCommand;

    private final String channelPrefix;

    protected AbstractChannel(
            @NotNull final String name,
            @NotNull final String messagePrefix,
            @NotNull final String toggleCommand,
            @NotNull final String channelPrefix) {
        this.name = name;
        this.messagePrefix = messagePrefix;
        this.toggleCommand = toggleCommand;
        this.channelPrefix = channelPrefix;
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
    public @NotNull String commandName() {
        return toggleCommand;
    }


    @Override
    public boolean isUseableBy(@NotNull final ChatUser user) {
        return user.player().hasPermission(ChannelUtils.USE_CHANNEL_PERMISSION + name());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatChannel that = (ChatChannel) o;
        return messagePrefix.equals(that.messagePrefix()) &&
                toggleCommand.equals(that.commandName()) &&
                channelPrefix.equals(that.channelPrefix());
    }
}
