package at.helpch.chatchat.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public interface Channel {

    @NotNull String name();

    @NotNull String messagePrefix();

    @NotNull String channelPrefix();

    @NotNull List<String> commandNames();

    Set<User> targets(@NotNull final User source);

    boolean isUseableBy(@NotNull final ChatUser user);
}
