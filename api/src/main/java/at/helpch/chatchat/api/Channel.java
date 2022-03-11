package at.helpch.chatchat.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Channel {

    @NotNull String messagePrefix();

    @NotNull String channelPrefix();

    @NotNull List<User> audience();

    @NotNull String commandName();
}
