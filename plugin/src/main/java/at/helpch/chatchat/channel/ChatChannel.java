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

    @Override
    public boolean isDefault() {
        return false;
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
