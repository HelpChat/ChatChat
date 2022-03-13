package at.helpch.chatchat.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface User {

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
