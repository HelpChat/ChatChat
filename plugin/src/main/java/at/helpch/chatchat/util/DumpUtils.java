package at.helpch.chatchat.util;

import at.helpch.chatchat.ChatChatPlugin;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class DumpUtils {

    @NotNull
    public static final String PASTE_URL = "https://paste.helpch.at/";
    @NotNull
    public static final List<String> FILES = List.of("settings.yml", "channels.yml", "formats.yml", "messages.yml");
    @NotNull
    private static final Gson gson = new Gson();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.LONG)
        .withLocale(Locale.US)
        .withZone(ZoneId.of("UTC"));

    private DumpUtils() {
        throw new AssertionError("Util classes should not be initialized");
    }

    @NotNull
    public static CompletableFuture<String> postDump(@NotNull final String dump) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final var connection = ((HttpURLConnection) new URL(PASTE_URL + "documents")
                    .openConnection());
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                connection.setDoOutput(true);

                connection.connect();

                try (final OutputStream stream = connection.getOutputStream()) {
                    stream.write(dump.getBytes(StandardCharsets.UTF_8));
                }

                try (final InputStream stream = connection.getInputStream()) {
                    final String json = CharStreams.toString(new InputStreamReader(stream, StandardCharsets.UTF_8));
                    return PASTE_URL + gson.fromJson(json, JsonObject.class).get("key").getAsString();
                }
            } catch (final IOException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    @NotNull
    public static Optional<String> createDump(@NotNull final ChatChatPlugin plugin, @Nullable final String fileName) {
        final var builder = new StringBuilder();

        builder.append("Generated On: ")
            .append(DATE_FORMAT.format(Instant.now()))
            .append(System.lineSeparator())
            .append(System.lineSeparator());

        builder.append("ChatChat Version: ")
            .append(plugin.getDescription().getVersion())
            .append(System.lineSeparator());

        builder.append("Java Version: ")
            .append(System.getProperty("java.version"))
            .append(System.lineSeparator());

        builder.append("Server Info:")
            .append(plugin.getServer().getBukkitVersion())
            .append('/')
            .append(plugin.getServer().getVersion())
            .append(System.lineSeparator())
            .append(System.lineSeparator());

        if (fileName == null || fileName.isEmpty()) {
            FILES.forEach(name -> createFileDump(plugin, builder, name));
            return Optional.of(builder.toString());
        }

        if (createFileDump(plugin, builder, fileName)) {
            return Optional.of(builder.toString());
        }

        return Optional.empty();
    }

    private static boolean createFileDump(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final StringBuilder builder,
        @NotNull final String fileName
    ) {
        final File configFile = new File(plugin.getDataFolder(), fileName);

        builder.append("---------------------------------------------")
            .append(System.lineSeparator())
            .append(System.lineSeparator());

        if (!configFile.exists() || !configFile.isFile()) {
            plugin.getLogger().warning(
                "Could not find file " + fileName + " in " + plugin.getDataFolder().getPath() + " while creating dump!"
            );

            builder.append("Could not find file ")
                .append(fileName)
                .append(" in ")
                .append(plugin.getDataFolder().getPath())
                .append(" while creating dump!")
                .append(System.lineSeparator())
                .append(System.lineSeparator());

            return false;
        }

        try {
            Files.readAllLines(configFile.toPath(), StandardCharsets.UTF_8).forEach(line ->
                builder.append(line).append(System.lineSeparator())
            );
        } catch (final IOException exception) {
            plugin.getLogger().warning("Something went wrong while reading the the file: " + configFile);
            exception.printStackTrace();
            return false;
        }

        return true;
    }
}
