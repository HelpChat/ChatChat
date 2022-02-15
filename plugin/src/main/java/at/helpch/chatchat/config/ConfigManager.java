package at.helpch.chatchat.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public final class ConfigManager
{

    private final Path dataFolder;
    private ChannelsHolder channels;
    private FormatsHolder formats;
    private SettingsHolder settings;

    public ConfigManager(@NotNull final Path dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Nullable // probably shouldn't be null? IDK
    public ChannelsHolder channels() {
        if (channels == null) {
            this.channels = new ConfigFactory(dataFolder).channels();
        }
        return this.channels;
    }

    @Nullable // probably shouldn't be null? IDK
    public SettingsHolder settings() {
        if (settings == null) {
            this.settings = new ConfigFactory(dataFolder).settings();
        }
        return this.settings;
    }

    @Nullable // probably shouldn't be null? IDK
    public FormatsHolder formats() {
        if (formats == null) {
            this.formats = new ConfigFactory(dataFolder).formats();
        }
        return this.formats;
    }
}
