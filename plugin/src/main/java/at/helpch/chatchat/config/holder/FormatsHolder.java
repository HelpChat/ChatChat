package at.helpch.chatchat.config.holder;

import at.helpch.chatchat.config.DefaultConfigObjects;
import at.helpch.chatchat.format.BasicFormat;
import at.helpch.chatchat.format.ChatFormat;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;

// configurate requires non-final fields
@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public final class FormatsHolder {

    private String defaultFormat = "default";

    private BasicFormat consoleFormat = DefaultConfigObjects.createDefaultConsoleFormat();

    private Map<String, ChatFormat> formats = Map.of(
            "other", DefaultConfigObjects.createOtherFormat(),
            defaultFormat, DefaultConfigObjects.createDefaultFormat());

    public @NotNull String defaultFormat() {
        return defaultFormat;
    }

    public @NotNull BasicFormat consoleFormat() {
        return consoleFormat;
    }

    public @NotNull Map<String, ChatFormat> formats() {
        return Map.copyOf(formats);
    }
}
