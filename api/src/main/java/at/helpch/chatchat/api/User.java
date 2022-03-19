package at.helpch.chatchat.api;

import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identified;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface User extends ForwardingAudience.Single, Identified {

    @NotNull Channel channel();

    void channel(@NotNull final Channel channel);

    @NotNull Format format();

    void format(@NotNull final Format format);

    @NotNull UUID uuid();

    boolean canSee(@NotNull final Channel channel);

    boolean canUse(@NotNull final Channel channel);

    @NotNull Optional<User> lastMessagedUser();

    void lastMessagedUser(@NotNull final User user);

    @NotNull Player player();
}
