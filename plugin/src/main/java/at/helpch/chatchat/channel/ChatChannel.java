package at.helpch.chatchat.channel;

import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.User;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;

@ConfigSerializable
public final class ChatChannel implements Channel {

    private String messagePrefix = "";

    private String toggleCommand = "global";

    private String channelPrefix = "[global]";

    private transient List<User> audience;

    // Configurate constructor
    public ChatChannel() {}

    private ChatChannel(
            @NotNull final String messagePrefix,
            @NotNull final String toggleCommand,
            @NotNull final String channelPrefix,
            @NotNull final List<User> audience) {
        this.messagePrefix = messagePrefix;
        this.toggleCommand = toggleCommand;
        this.channelPrefix = channelPrefix;
        this.audience = audience;
    }

    public static @NotNull ChatChannel of(
            @NotNull final String messagePrefix,
            @NotNull final String toggleCommand,
            @NotNull final String channelPrefix,
            @NotNull final List<User> audience) {
        return new ChatChannel(messagePrefix, toggleCommand, channelPrefix, audience);
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
        return Collections.emptyList();
    }

    @Override
    public @NotNull String commandName() {
        return toggleCommand;
    }
}
