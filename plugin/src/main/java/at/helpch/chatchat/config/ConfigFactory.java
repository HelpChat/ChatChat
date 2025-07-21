package at.helpch.chatchat.config;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.format.Format;
import at.helpch.chatchat.api.format.PriorityFormat;
import at.helpch.chatchat.api.holder.GlobalFormatsHolder;
import at.helpch.chatchat.config.holder.AddonsHolder;
import at.helpch.chatchat.config.holder.ChannelsHolder;
import at.helpch.chatchat.config.holder.ExtensionsHolder;
import at.helpch.chatchat.config.holder.GlobalFormatsHolderImpl;
import at.helpch.chatchat.config.holder.MessagesHolder;
import at.helpch.chatchat.config.holder.MiniPlaceholdersHolder;
import at.helpch.chatchat.config.holder.SettingsHolder;
import at.helpch.chatchat.config.mapper.AddonsMapper;
import at.helpch.chatchat.config.mapper.SimpleFormatMapper;
import at.helpch.chatchat.config.mapper.ChannelMapMapper;
import at.helpch.chatchat.config.mapper.MiniMessageComponentMapper;
import at.helpch.chatchat.config.mapper.MiniPlaceholderMapper;
import at.helpch.chatchat.config.mapper.PriorityFormatMapper;
import at.helpch.chatchat.format.SimpleFormat;
import at.helpch.chatchat.format.ChatFormat;
import at.helpch.chatchat.placeholder.MiniPlaceholderImpl;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.serializer.configurate4.ConfigurateComponentSerializer;
import net.kyori.adventure.text.Component;
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
    private @NotNull final ChatChatPlugin plugin;

    public ConfigFactory(@NotNull final Path dataFolder, @NotNull final ChatChatPlugin plugin) {
        this.dataFolder = dataFolder;
        this.plugin = plugin;
    }

    public @NotNull ChannelsHolder channels() {
        final var config = create(ChannelsHolder.class, "channels.yml");
        return Objects.requireNonNullElseGet(config, ChannelsHolder::new);
    }

    public @NotNull GlobalFormatsHolder formats() {
        final var config = create(GlobalFormatsHolderImpl.class, "formats.yml");
        return Objects.requireNonNullElseGet(config, GlobalFormatsHolderImpl::new);
    }

    public @NotNull SettingsHolder settings() {
        final var config = create(SettingsHolder.class, "settings.yml");
        return Objects.requireNonNullElseGet(config, SettingsHolder::new);
    }

    public @NotNull MessagesHolder messages() {
        final var config = create(MessagesHolder.class, "messages.yml");
        return Objects.requireNonNullElseGet(config, MessagesHolder::new);
    }

    public @NotNull ExtensionsHolder extensions() {
        final var config = create(ExtensionsHolder.class, "extensions.yml");
        return Objects.requireNonNullElseGet(config, ExtensionsHolder::new);
    }

    public @NotNull MiniPlaceholdersHolder miniPlaceholders() {
        final var config = create(MiniPlaceholdersHolder.class, "placeholders.yml");
        return Objects.requireNonNullElseGet(config, MiniPlaceholdersHolder::new);
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
            }

            loader.save(node);
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
                    .register(Component.class, new MiniMessageComponentMapper())

                    .register(SimpleFormat.class, new SimpleFormatMapper())

                    .register(PriorityFormat.class, new PriorityFormatMapper())
                    .register(ChatFormat.class, new PriorityFormatMapper())

                    .register(MiniPlaceholderImpl.class, new MiniPlaceholderMapper())

                    .register(AddonsHolder.class, new AddonsMapper())

                    .register(new TypeToken<>() {}, new ChannelMapMapper(plugin))
                    .registerAll(ConfigurateComponentSerializer.configurate().serializers())))
            .nodeStyle(NodeStyle.BLOCK)
            .indent(2)
            .build();
    }
}
