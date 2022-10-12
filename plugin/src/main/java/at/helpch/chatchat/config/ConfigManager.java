package at.helpch.chatchat.config;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.config.holder.ChannelsHolder;
import at.helpch.chatchat.config.holder.FormatsHolder;
import at.helpch.chatchat.config.holder.MessagesHolder;
import at.helpch.chatchat.config.holder.SettingsHolder;
import at.helpch.chatchat.format.ChatFormat;
import at.helpch.chatchat.format.DefaultFormatFactory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class ConfigManager {

    private @NotNull final ChatChatPlugin plugin;
    private ChannelsHolder channels;
    private FormatsHolder formats;
    private SettingsHolder settings;
    private MessagesHolder messages;
    private final ConfigFactory factory;

    public ConfigManager(final @NotNull ChatChatPlugin plugin, @NotNull final Path dataFolder) {
        this.plugin = plugin;
        this.factory = new ConfigFactory(dataFolder, plugin);
    }

    public void reload() {
        messages = null;
        channels = null;
        formats = null;
        settings = null;

        messages();

        channels();
        var defaultChannel = channels.channels().get(channels.defaultChannel());
        if (!(defaultChannel instanceof ChatChannel)) defaultChannel = DefaultConfigObjects.createDefaultChannel();
        ChatChannel.defaultChannel((ChatChannel) defaultChannel);

        settings();

        formats();
        final var defaultFormat = formats.formats().getOrDefault(formats.defaultFormat(), DefaultFormatFactory.createDefaultFormat());
        ChatFormat.defaultFormat(defaultFormat);
    }

    public @NotNull ChannelsHolder channels() {
        if (channels == null) {
            this.channels = factory.channels();
        }
        return this.channels;
    }

    public @NotNull SettingsHolder settings() {
        if (settings == null) {
            this.settings = factory.settings();
        }
        return this.settings;
    }

    public @NotNull FormatsHolder formats() {
        if (formats == null) {
            this.formats = factory.formats();
        }
        return this.formats;
    }

    public @NotNull MessagesHolder messages() {
        if (messages == null) {
            this.messages = factory.messages();
        }
        return this.messages;
    }
}
