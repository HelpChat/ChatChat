package at.helpch.chatchat.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Channel {

    boolean isDefault();

    @NotNull String messagePrefix();

    @NotNull String channelPrefix();

    @NotNull List<User> usersInChannel();

    @NotNull String commandName();
}
