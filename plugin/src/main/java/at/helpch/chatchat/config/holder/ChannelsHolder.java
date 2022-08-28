package at.helpch.chatchat.config.holder;

import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.config.DefaultConfigObjects;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;

// configurate requires non-final fields
@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public final class ChannelsHolder {

    private String defaultChannel = "default";

    private Map<String, Channel> channels = Map.of(
            "staff", DefaultConfigObjects.createStaffChannel(),
            defaultChannel, DefaultConfigObjects.createDefaultChannel());

    public @NotNull String defaultChannel() {
        return defaultChannel;
    }

    public @NotNull Map<String, Channel> channels() {
        return Map.copyOf(channels);
    }
}
