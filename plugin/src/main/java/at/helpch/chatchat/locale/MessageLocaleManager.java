package at.helpch.chatchat.locale;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public final class MessageLocaleManager {

    private static final String ENGLISH = "en_us";

    private final ChatChatPlugin plugin;
    private final Path dataFolder;
    private volatile Map<LocaleMessage, String> english = Map.of();
    private volatile Map<String, Map<LocaleMessage, String>> translations = Map.of();

    public MessageLocaleManager(@NotNull final ChatChatPlugin plugin, @NotNull final Path dataFolder) {
        this.plugin = plugin;
        this.dataFolder = dataFolder;
    }

    public void reload() {
        final var defaultMessages = new EnumMap<LocaleMessage, String>(LocaleMessage.class);
        try {
            final var resource = YamlConfigurationLoader.builder()
                .source(() -> new BufferedReader(new InputStreamReader(
                    Objects.requireNonNull(plugin.getResource("locales/en_us.yml")), StandardCharsets.UTF_8)))
                .build().load();
            defaultMessages.putAll(readMessages(resource));
        } catch (final IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "Could not load bundled English messages", exception);
        }

        final var loadedTranslations = new HashMap<String, Map<LocaleMessage, String>>();
        final var localesFolder = dataFolder.resolve("locales");
        try {
            Files.createDirectories(localesFolder);
            if (Files.notExists(localesFolder.resolve("en_us.yml"))) {
                plugin.saveResource("locales/en_us.yml", false);
            }
            try (final var files = Files.list(localesFolder)) {
                files.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".yml"))
                    .forEach(path -> {
                        final var fileName = path.getFileName().toString();
                        final var locale = normalize(fileName.substring(0, fileName.length() - 4));
                        if (locale == null) {
                            plugin.getLogger().warning("Ignoring invalid locale file name: " + fileName);
                            return;
                        }
                        final var messages = new EnumMap<LocaleMessage, String>(LocaleMessage.class);
                        loadFile(path, messages);
                        loadedTranslations.put(locale, Map.copyOf(messages));
                    });
            }
        } catch (final IOException exception) {
            plugin.getLogger().log(Level.WARNING, "Could not load locale files", exception);
        }

        english = Map.copyOf(defaultMessages);
        translations = Map.copyOf(loadedTranslations);
    }

    public @NotNull String template(@NotNull final User recipient, @NotNull final LocaleMessage message) {
        final var configured = normalize(plugin.configManager().settings().defaultLocale());
        final var defaultLocale = configured == null ? ENGLISH : configured;
        var locale = defaultLocale;
        if (recipient instanceof ChatUser) {
            final var player = ((ChatUser) recipient).player();
            if (player.isPresent()) {
                final var selected = normalize(player.get().getLocale());
                if (selected != null) {
                    locale = selected;
                }
            }
        }

        final var localized = translations.getOrDefault(locale, Map.of()).get(message);
        if (localized != null) {
            return localized;
        }
        final var serverDefault = translations.getOrDefault(defaultLocale, Map.of()).get(message);
        if (serverDefault != null) {
            return serverDefault;
        }
        return english.getOrDefault(message, "<red>Missing message: " + message.key());
    }

    private void loadFile(@NotNull final Path path, @NotNull final Map<LocaleMessage, String> messages) {
        try {
            messages.putAll(readMessages(YamlConfigurationLoader.builder().path(path).build().load()));
        } catch (final IOException exception) {
            plugin.getLogger().log(Level.WARNING, "Could not load messages from " + path, exception);
        }
    }

    private static @NotNull Map<LocaleMessage, String> readMessages(@NotNull final ConfigurationNode node) {
        final var messages = new EnumMap<LocaleMessage, String>(LocaleMessage.class);
        for (final var message : LocaleMessage.values()) {
            final var value = node.node(message.key()).getString();
            if (value != null) {
                messages.put(message, value);
            }
        }
        return messages;
    }

    private static String normalize(@NotNull final String locale) {
        final var normalized = locale.toLowerCase(Locale.ROOT).replace('-', '_');
        return normalized.matches("[a-z]{2,3}(?:_[a-z0-9]{2,8})*") ? normalized : null;
    }
}
