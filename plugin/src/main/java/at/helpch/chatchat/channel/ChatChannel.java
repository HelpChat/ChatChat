package at.helpch.chatchat.channel;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.config.DefaultConfigObjects;
import at.helpch.chatchat.util.ChannelUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public final class ChatChannel implements Channel, ForwardingAudience.Single {

    private static ChatChannel defaultChannel = DefaultConfigObjects.createDefaultChannel();

    private final String name;

    private final String messagePrefix;

    private final String toggleCommand;

    private final String channelPrefix;

    public ChatChannel(
            @NotNull final String name,
            @NotNull final String messagePrefix,
            @NotNull final String toggleCommand,
            @NotNull final String channelPrefix) {
        this.name = name;
        this.messagePrefix = messagePrefix;
        this.toggleCommand = toggleCommand;
        this.channelPrefix = channelPrefix;
    }

    @Override
    public @NotNull String name() {
        return name;
    }

    @Override
    public @NotNull String messagePrefix() {
        return messagePrefix;
    }

    @Override
    public @NotNull String channelPrefix() {
        return channelPrefix;
    }

    @Override
    public @NotNull String commandName() {
        return toggleCommand;
    }

    @Override
    public @NotNull Audience audience() {
        return ChatChatPlugin.audiences().permission(ChannelUtils.SEE_CHANNEL_PERMISSION + name());
    }

    @Override
    public boolean isUseableBy(@NotNull final ChatUser user) {
        return user.player().hasPermission(ChannelUtils.USE_CHANNEL_PERMISSION + name());
    }

    public static @NotNull ChatChannel defaultChannel() {
        return defaultChannel;
    }

    public static void defaultChannel(@NotNull final ChatChannel toSet) {
        defaultChannel = toSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatChannel that = (ChatChannel) o;
        return messagePrefix.equals(that.messagePrefix) &&
                toggleCommand.equals(that.toggleCommand) &&
                channelPrefix.equals(that.channelPrefix);
    }

    @Override
    public String toString() {
        return "ChatChannel{" +
                "name=" + name +
                ", messagePrefix='" + messagePrefix + '\'' +
                ", toggleCommand='" + toggleCommand + '\'' +
                ", channelPrefix='" + channelPrefix +
                '}';
    }
}
