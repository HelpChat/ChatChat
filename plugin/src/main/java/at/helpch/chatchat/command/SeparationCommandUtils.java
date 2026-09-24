package at.helpch.chatchat.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

final class SeparationCommandUtils {

    private SeparationCommandUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    static @Nullable OfflinePlayer findPlayer(final String input) {
        OfflinePlayer player;
        try {
            player = Bukkit.getOfflinePlayer(UUID.fromString(input));
        } catch (final IllegalArgumentException ignored) {
            player = Bukkit.getOfflinePlayer(input);
        }
        return player.isOnline() || player.hasPlayedBefore() ? player : null;
    }

    static String name(final OfflinePlayer player, final String input) {
        return player.getName() == null ? input : player.getName();
    }
}
