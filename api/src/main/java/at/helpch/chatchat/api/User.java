package at.helpch.chatchat.api;

import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identified;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface User extends ForwardingAudience.Single, Identified {

    @NotNull Channel channel();

    void channel(@NotNull final Channel channel);

    @NotNull Format format();

    void format(@NotNull final Format format);

    @NotNull UUID uuid();
}
