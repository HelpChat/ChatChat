package at.helpch.chatchat.api;

import net.kyori.adventure.audience.ForwardingAudience;
import org.jetbrains.annotations.NotNull;

public interface Channel extends ForwardingAudience {

    @NotNull String name();

    @NotNull String messagePrefix();

    @NotNull String channelPrefix();

    @NotNull String commandName();

    @Override
    @NotNull Iterable<User> audiences();
}
