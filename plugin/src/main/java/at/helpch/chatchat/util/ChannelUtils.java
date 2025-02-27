package at.helpch.chatchat.util;

import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import at.helpch.chatchat.channel.ChatChannel;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ChannelUtils {
    public static final String BASE_CHANNEL_PERMISSION = "chatchat.channel.";
    public static final String SEE_CHANNEL_PERMISSION = BASE_CHANNEL_PERMISSION + "see.";
    public static final String USE_CHANNEL_PERMISSION = BASE_CHANNEL_PERMISSION + "use.";
    public static final String BYPASS_RADIUS_CHANNEL_PERMISSION = BASE_CHANNEL_PERMISSION + "bypass-radius";

    private ChannelUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static @NotNull Channel findDefaultChannel(
            @NotNull final Map<String, Channel> channels,
            @NotNull final String defaultChannel) {
        final var channel = channels.get(defaultChannel);
        return Objects.requireNonNullElseGet(channel, ChatChannel::defaultChannel);
    }

    public static @NotNull Optional<Channel> findChannelByPrefix(
            @NotNull final List<Channel> channels,
            @NotNull final String input) {
        return channels.stream()
                .filter(channel -> !channel.messagePrefix().isEmpty()) // ignore empty prefixes
                .filter(channel -> input.startsWith(channel.messagePrefix()))
                .findFirst();
    }

    public static boolean isTargetWithinRadius(
            @NotNull final User source,
            @NotNull final User target,
            final int radius) {
        if (!(target instanceof ChatUser)) {
            return true;
        }

        final ChatUser targetChatUser = (ChatUser) target;

        if (target.hasPermission(BYPASS_RADIUS_CHANNEL_PERMISSION) && !targetChatUser.rangedChat()) {
            return true;
        }

        if (radius != -1 && source instanceof ChatUser) {
            var sourcePlayer = ((ChatUser) source).player();
            var targetPlayer = targetChatUser.player();

            if (sourcePlayer.isEmpty() || targetPlayer.isEmpty()) {
                return false;
            }

            final Location sourceLocation = sourcePlayer.get().getLocation();
            final Location targetLocation = targetPlayer.get().getLocation();

            final World sourceWorld = sourceLocation.getWorld();
            final World targetWorld = targetLocation.getWorld();

            if(sourceWorld != null && targetWorld != null && !sourceWorld.getUID().equals(targetWorld.getUID())) {
                return false;
            }

            final int relativeX = targetLocation.getBlockX() - sourceLocation.getBlockX();
            final int relativeZ = targetLocation.getBlockZ() - sourceLocation.getBlockZ();

            return relativeX*relativeX + relativeZ*relativeZ <= radius*radius;
        }

        return true;
    }
}
