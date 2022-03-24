package at.helpch.chatchat.config.holders;

import at.helpch.chatchat.config.DefaultConfigObjects;
import at.helpch.chatchat.format.ChatFormat;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public final class FormatsHolder {

    private String defaultFormat = "default";

    private Map<String, ChatFormat> formats = Map.of(
            "other", DefaultConfigObjects.createOtherFormat(),
            defaultFormat, DefaultConfigObjects.createDefaultFormat());

    public @NotNull String defaultFormat() {
        return defaultFormat;
    }

    public @NotNull Map<String, ChatFormat> formats() {
        return Map.copyOf(formats);
    }
}
