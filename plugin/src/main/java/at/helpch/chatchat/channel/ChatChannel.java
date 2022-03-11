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

    private transient boolean isDefault = false;

    private transient List<User> usersInChannel;

    // Configurate constructor
    public ChatChannel() {}

    private ChatChannel(
            @NotNull final String messagePrefix,
            @NotNull final String toggleCommand,
            @NotNull final String channelPrefix,
            final boolean isDefault,
            @NotNull final List<User> usersInChannel) {
        this.messagePrefix = messagePrefix;
        this.toggleCommand = toggleCommand;
        this.channelPrefix = channelPrefix;
        this.isDefault = isDefault;
        this.usersInChannel = usersInChannel;
    }

    public static @NotNull ChatChannel of(
            @NotNull final String messagePrefix,
            @NotNull final String toggleCommand,
            @NotNull final String channelPrefix,
            final boolean isDefault,
            @NotNull final List<User> usersInChannel) {
        return new ChatChannel(messagePrefix, toggleCommand, channelPrefix, isDefault, usersInChannel);
    }

    @Override
    public boolean isDefault() {
        return isDefault;
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
    public @NotNull List<User> usersInChannel() {
        return Collections.emptyList();
    }

    @Override
    public @NotNull String commandName() {
        return toggleCommand;
    }
}
