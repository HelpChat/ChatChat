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
public final class ChatChannel extends AbstractChannel implements ForwardingAudience.Single {

    private static ChatChannel defaultChannel = DefaultConfigObjects.createDefaultChannel();

    public ChatChannel(
            @NotNull final String name,
            @NotNull final String messagePrefix,
            @NotNull final String toggleCommand,
            @NotNull final String channelPrefix) {
        super(name, messagePrefix, toggleCommand, channelPrefix);
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
    public String toString() {
        return "ChatChannel{" +
                "name=" + name() +
                ", messagePrefix='" + messagePrefix() + '\'' +
                ", toggleCommand='" + commandName() + '\'' +
                ", channelPrefix='" + channelPrefix() +
                '}';
    }
}
