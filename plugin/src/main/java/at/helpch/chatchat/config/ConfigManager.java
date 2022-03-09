package at.helpch.chatchat.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public final class ConfigManager {

    private @NotNull final Path dataFolder;
    private ChannelsHolder channels;
    private FormatsHolder formats;
    private SettingsHolder settings;

    public ConfigManager(@NotNull final Path dataFolder) {
        this.dataFolder = dataFolder;
    }

    public void reload() {
        channels = null;
        formats = null;
        settings = null;
        channels();
        settings();
        formats();
    }

    // probably shouldn't be null? IDK
    public @Nullable ChannelsHolder channels() {
        if (channels == null) {
            this.channels = new ConfigFactory(dataFolder).channels();
        }
        return this.channels;
    }

    // probably shouldn't be null? IDK
    public @Nullable SettingsHolder settings() {
        if (settings == null) {
            this.settings = new ConfigFactory(dataFolder).settings();
        }
        return this.settings;
    }

    // probably shouldn't be null? IDK
    public @Nullable FormatsHolder formats() {
        if (formats == null) {
            this.formats = new ConfigFactory(dataFolder).formats();
        }
        return this.formats;
    }
}
