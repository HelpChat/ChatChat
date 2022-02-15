package at.helpch.chatchat.config;

import at.helpch.chatchat.channel.ChatChannel;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public final class ChannelsHolder {

    private String defaultChannel = "default";
    private Map<String, ChatChannel> channels = new HashMap<>();
}
