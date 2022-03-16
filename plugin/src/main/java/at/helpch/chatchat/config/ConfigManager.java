package at.helpch.chatchat.config;

import at.helpch.chatchat.config.holders.ChannelsHolder;
import at.helpch.chatchat.config.holders.FormatsHolder;
import at.helpch.chatchat.config.holders.SettingsHolder;
import org.jetbrains.annotations.NotNull;

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

    public @NotNull ChannelsHolder channels() {
        if (channels == null) {
            this.channels = new ConfigFactory(dataFolder).channels();
        }
        return this.channels;
    }

    public @NotNull SettingsHolder settings() {
        if (settings == null) {
            this.settings = new ConfigFactory(dataFolder).settings();
        }
        return this.settings;
    }

    public @NotNull FormatsHolder formats() {
        if (formats == null) {
            this.formats = new ConfigFactory(dataFolder).formats();
        }
        return this.formats;
    }
}
