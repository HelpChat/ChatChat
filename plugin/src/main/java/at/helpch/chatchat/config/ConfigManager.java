package at.helpch.chatchat.config;

import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.config.holders.ChannelsHolder;
import at.helpch.chatchat.config.holders.FormatsHolder;
import at.helpch.chatchat.config.holders.SettingsHolder;
import at.helpch.chatchat.format.ChatFormat;
import at.helpch.chatchat.format.DefaultFormatFactory;
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
        final var defaultChannel = channels.channels().getOrDefault(channels.defaultChannel(), DefaultConfigObjects.createDefaultChannel());
        // fixme - make this safe
        ChatChannel.defaultChannel((ChatChannel) defaultChannel);

        settings();

        formats();
        final var defaultFormat = formats.formats().getOrDefault(formats.defaultFormat(), DefaultFormatFactory.createDefaultFormat());
        ChatFormat.defaultFormat(defaultFormat);
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
