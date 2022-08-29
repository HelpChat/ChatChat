package at.helpch.chatchat.api.user;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface UsersHolder {
    @NotNull User getUser(@NotNull UUID uuid);
    @NotNull User getUser(@NotNull CommandSender user);

    void removeUser(@NotNull UUID uuid);
    void removeUser(@NotNull Player user);

    @NotNull Collection<User> users();
}
