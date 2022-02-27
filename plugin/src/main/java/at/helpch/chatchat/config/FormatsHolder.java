package at.helpch.chatchat.config;

import at.helpch.chatchat.format.ChatFormat;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public final class FormatsHolder {

    private String defaultFormat = "default";
    private Map<String, ChatFormat> formats = new HashMap<>();

}
