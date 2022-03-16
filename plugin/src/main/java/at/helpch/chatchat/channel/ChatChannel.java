package at.helpch.chatchat.channel;

import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.User;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public final class ChatChannel implements Channel {

    private final String name;

    private final String messagePrefix;

    private final String toggleCommand;

    private final String channelPrefix;

    private final List<User> audience;

    public ChatChannel(
            @NotNull final String name,
            @NotNull final String messagePrefix,
            @NotNull final String toggleCommand,
            @NotNull final String channelPrefix,
            @NotNull final List<User> audience) {
        this.name = name;
        this.messagePrefix = messagePrefix;
        this.toggleCommand = toggleCommand;
        this.channelPrefix = channelPrefix;
        this.audience = audience;
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
    public @NotNull List<User> audience() {
        return audience;
    }

    @Override
    public @NotNull String commandName() {
        return toggleCommand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatChannel that = (ChatChannel) o;
        return messagePrefix.equals(that.messagePrefix) &&
                toggleCommand.equals(that.toggleCommand) &&
                channelPrefix.equals(that.channelPrefix) &&
                audience.equals(that.audience);
    }

    @Override
    public String toString() {
        return "ChatChannel{" +
                "name=" + name +
                ", messagePrefix='" + messagePrefix + '\'' +
                ", toggleCommand='" + toggleCommand + '\'' +
                ", channelPrefix='" + channelPrefix + '\'' +
                ", audience=" + audience +
                '}';
    }
}
