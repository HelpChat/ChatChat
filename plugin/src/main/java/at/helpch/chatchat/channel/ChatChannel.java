package at.helpch.chatchat.channel;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.User;
import at.helpch.chatchat.config.DefaultConfigObjects;
import at.helpch.chatchat.util.ChannelUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ConfigSerializable
public final class ChatChannel extends AbstractChannel {

    private static ChatChannel defaultChannel = DefaultConfigObjects.createDefaultChannel();

    public ChatChannel(
            @NotNull final String name,
            @NotNull final String messagePrefix,
            @NotNull final List<String> toggleCommands,
            @NotNull final String channelPrefix,
            final int radius) {
        super(name, messagePrefix, toggleCommands, channelPrefix, radius);
    }

    public static @NotNull ChatChannel defaultChannel() {
        return defaultChannel;
    }

    public static void defaultChannel(@NotNull final ChatChannel toSet) {
        defaultChannel = toSet;
    }

    private final ChatChatPlugin plugin = ChatChatPlugin.getPlugin(ChatChatPlugin.class);

    @Override
    public String toString() {
        return "ChatChannel{" +
                "name=" + name() +
                ", messagePrefix='" + messagePrefix() + '\'' +
                ", toggleCommands='" + commandNames() + '\'' +
                ", channelPrefix='" + channelPrefix() + '\'' +
                ", radius='" + radius() +
                '}';
    }

    @Override
    public Set<User> targets(final @NotNull User source) {
        return plugin.usersHolder().users().stream().filter(user ->
                !(user instanceof ChatUser) ||
                    ((ChatUser) user).player().hasPermission(ChannelUtils.SEE_CHANNEL_PERMISSION + name()))
                .filter(user -> ChannelUtils.isTargetWithinRadius(source, user, radius()))
                .collect(Collectors.toUnmodifiableSet());
    }
}
