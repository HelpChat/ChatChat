package at.helpch.chatchat.config;

import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.config.holders.ChannelsHolder;
import at.helpch.chatchat.config.holders.FormatsHolder;
import at.helpch.chatchat.config.holders.SettingsHolder;
import at.helpch.chatchat.config.mapper.ChannelMapper;
import at.helpch.chatchat.config.mapper.ChatFormatMapper;
import at.helpch.chatchat.config.mapper.PMFormatMapper;
import at.helpch.chatchat.format.ChatFormat;
import at.helpch.chatchat.format.PMFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class ConfigFactory {

    private @NotNull final Path dataFolder;

    public ConfigFactory(@NotNull final Path dataFolder) {
        this.dataFolder = dataFolder;
    }

    public @NotNull ChannelsHolder channels() {
        final var config = create(ChannelsHolder.class, "channels.yml");
        return Objects.requireNonNullElseGet(config, ChannelsHolder::new);
    }

    public @NotNull FormatsHolder formats() {
        final var config = create(FormatsHolder.class, "formats.yml");
        return Objects.requireNonNullElseGet(config, FormatsHolder::new);
    }

    public @NotNull SettingsHolder settings() {
        final var config = create(SettingsHolder.class, "settings.yml");
        return Objects.requireNonNullElseGet(config, SettingsHolder::new);
    }

    private @Nullable <T> T create(@NotNull final Class<T> clazz, @NotNull final String fileName) {
        try {
            if (!Files.exists(dataFolder)) {
                Files.createDirectories(dataFolder);
            }

            final var path = dataFolder.resolve(fileName);

            final var loader = loader(path);
            final var node = loader.load();
            final var config = node.get(clazz);

            if (!Files.exists(path)) {
                Files.createFile(path);
                node.set(clazz, config);
                loader.save(node);
            }

            return config;
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private @NotNull YamlConfigurationLoader loader(@NotNull final Path path) {
        return YamlConfigurationLoader.builder()
                .path(path)
                .defaultOptions(options -> options.shouldCopyDefaults(true)
                        .header("https://wiki.helpch.at")
                        .serializers(build -> build
                                .register(ChatFormat.class, new ChatFormatMapper())
                                .register(ChatChannel.class, new ChannelMapper())
                                .register(PMFormat.class, new PMFormatMapper())))
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .build();
    }
}
