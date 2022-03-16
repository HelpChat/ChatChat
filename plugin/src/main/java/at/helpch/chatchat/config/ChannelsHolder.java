package at.helpch.chatchat.config;

import at.helpch.chatchat.channel.ChatChannel;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;

@ConfigSerializable
public final class ChannelsHolder {

    private String defaultChannel = "default";

    private Map<String, ChatChannel> channels = Map.of(
            "staff", DefaultConfigObjects.createStaffChannel(),
            defaultChannel, DefaultConfigObjects.createDefaultChannel());

    public @NotNull String defaultChannel() {
        return defaultChannel;
    }

    public @NotNull Map<String, ChatChannel> channels() {
        return Map.copyOf(channels);
    }
}
