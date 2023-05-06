package at.helpch.chatchat.config;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.holder.GlobalFormatsHolder;
import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.config.holder.ChannelsHolder;
import at.helpch.chatchat.config.holder.ExtensionsHolder;
import at.helpch.chatchat.config.holder.MessagesHolder;
import at.helpch.chatchat.config.holder.MiniPlaceholdersHolder;
import at.helpch.chatchat.config.holder.SettingsHolder;
import at.helpch.chatchat.format.ChatFormat;
import at.helpch.chatchat.format.DefaultFormatFactory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class ConfigManager {

    private @NotNull final ChatChatPlugin plugin;
    private ChannelsHolder channels;
    private GlobalFormatsHolder formats;
    private SettingsHolder settings;
    private MessagesHolder messages;
    private ExtensionsHolder extensions;
    private MiniPlaceholdersHolder miniPlaceholders;
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
        extensions = null;
        miniPlaceholders = null;

        messages();
        extensions();
        plugin.getLogger().info("Whenever making changes to extensions.yml, restart the server to make sure all changes are applied.");

        channels();
        final var defaultChannel = channels.channels().get(channels.defaultChannel());
        if (defaultChannel instanceof ChatChannel) {
            ChatChannel.defaultChannel(defaultChannel);
        } else {
            plugin.getLogger().warning(
                "Could not find a channel named " + channels.defaultChannel() + "." + System.lineSeparator() +
                    "Using an internal channel as the default channel."
            );
            ChatChannel.defaultChannel(DefaultConfigObjects.createDefaultChannel());
        }
        plugin.usersHolder().users().forEach(user -> user.channel(channels().channels().getOrDefault(user.channel().name(), ChatChannel.defaultChannel())));

        settings();

        formats();
        final var defaultFormat = formats.formats().get(formats.defaultFormat());
        if (defaultFormat instanceof ChatFormat) {
            ChatFormat.defaultFormat(defaultFormat);
        } else {
            ChatFormat.defaultFormat(DefaultFormatFactory.createDefaultFormat());
            plugin.getLogger().warning(
                "Could not find a format named " + formats.defaultFormat() + "." + System.lineSeparator() +
                    "Using an internal format as the default format."
            );
        }

        miniPlaceholders();
        plugin.miniPlaceholdersManager().clear();
        miniPlaceholders.placeholders().forEach(placeholder -> plugin.miniPlaceholdersManager().addPlaceholder(placeholder));
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

    public @NotNull GlobalFormatsHolder formats() {
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

    public @NotNull ExtensionsHolder extensions() {
        if (extensions == null) {
            this.extensions = factory.extensions();
        }
        return this.extensions;
    }

    public @NotNull MiniPlaceholdersHolder miniPlaceholders() {
        if (miniPlaceholders == null) {
            this.miniPlaceholders = factory.miniPlaceholders();
        }
        return this.miniPlaceholders;
    }
}
