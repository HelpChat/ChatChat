package at.helpch.chatchat.channel;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.holder.FormatsHolder;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import at.helpch.chatchat.command.IgnoreCommand;
import at.helpch.chatchat.config.DefaultConfigObjects;
import at.helpch.chatchat.util.ChannelUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@ConfigSerializable
public final class ChatChannel extends AbstractChannel {

    private static Channel defaultChannel = DefaultConfigObjects.createDefaultChannel();

    public ChatChannel(
        @NotNull final String name,
        @NotNull final String messagePrefix,
        @NotNull final List<String> toggleCommands,
        @NotNull final String channelPrefix,
        @NotNull final FormatsHolder formats,
        final int radius
    ) {
        super(name, messagePrefix, toggleCommands, channelPrefix, formats, radius);
    }

    public static @NotNull Channel defaultChannel() {
        return defaultChannel;
    }

    public static void defaultChannel(@NotNull final Channel toSet) {
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

        final Predicate<User> filterIgnores = user -> user instanceof ChatUser &&
            (!user.ignoredUsers().contains(source.uuid()) || source.hasPermission(IgnoreCommand.IGNORE_BYPASS_PERMISSION));

        if (ChatChannel.defaultChannel().equals(this)) {
            return plugin.usersHolder().users().stream()
                .filter(User::chatEnabled) // Make sure the user has their chat enabled
                .filter(filterIgnores)
                .filter(user -> ChannelUtils.isTargetWithinRadius(source, user, radius()))
                .collect(Collectors.toSet());
        }

        return plugin.usersHolder().users().stream().filter(user ->
                user.hasPermission(ChannelUtils.SEE_CHANNEL_PERMISSION + name()))
            .filter(User::chatEnabled) // Make sure the user has their chat enabled
            .filter(filterIgnores)
            .filter(user -> ChannelUtils.isTargetWithinRadius(source, user, radius()))
            .collect(Collectors.toSet());
    }

}
