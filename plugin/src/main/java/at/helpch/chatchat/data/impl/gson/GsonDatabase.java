package at.helpch.chatchat.data.impl.gson;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.data.base.Database;
import at.helpch.chatchat.user.ChatUserImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Level;

public class GsonDatabase implements Database {
    private final ChatChatPlugin plugin;
    private final Gson gson;
    private final File usersDirectory;

    public GsonDatabase(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
        final var chatUserAdapter = new ChatUserSerializer(plugin);
        this.gson = new GsonBuilder()
            .registerTypeAdapter(ChatUser.class, chatUserAdapter)
            .registerTypeAdapter(ChatUserImpl.class, chatUserAdapter)
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
        usersDirectory = new File(plugin.getDataFolder(), "users");
        if (!usersDirectory.exists() && !usersDirectory.mkdirs()) {
            plugin.getLogger().warning("Failed to create users directory!");
        }
    }

    @Override
    public @NotNull ChatUser loadChatUser(@NotNull final UUID uuid) {
        final var userFile = new File(usersDirectory, uuid + ".json");

        if (!userFile.exists()) {
            final var user = new ChatUserImpl(uuid);
            final var channel = ChatChannel.defaultChannel();

            user.channel(channel);
            return user;
        }

        try(final var reader = new FileReader(userFile)) {
            final var user = gson.fromJson(reader, ChatUser.class);
            if (user == null) {
                throw new JsonParseException("User file contained null");
            }
            return user;
        } catch (final JsonParseException exception) { // Handles invalid JSON
            plugin.getLogger().log(Level.WARNING, "Could not load user " + uuid + " from " + userFile, exception);
            try {
                final var backupFile = backupInvalidUserFile(userFile.toPath(), uuid);
                plugin.getLogger().warning("Saved invalid user data to " + backupFile);
            } catch (final IOException ioException) {
                plugin.getLogger().log(
                    Level.SEVERE,
                    "Could not back up invalid user data for " + uuid + ". Disabling ChatChat to avoid overwriting it.",
                    ioException
                );

                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }

            final var user = new ChatUserImpl(uuid);
            final var channel = ChatChannel.defaultChannel();

            user.channel(channel);
            return user;

        } catch (final IOException exception) { // Handles missing file
            final var user = new ChatUserImpl(uuid);
            final var channel = ChatChannel.defaultChannel();

            user.channel(channel);
            return user;
        }
    }

    static @NotNull Path backupInvalidUserFile(@NotNull final Path userFile, @NotNull final UUID uuid) throws IOException {
        for (int index = 0; ; index++) {
            final var backupName = uuid + "-backup" + (index == 0 ? "" : "-" + index) + ".json";
            final var backupFile = userFile.resolveSibling(backupName);
            try {
                return Files.copy(userFile, backupFile);
            } catch (final FileAlreadyExistsException ignored) {
                // Keep previous backups and try the next available name.
            }
        }
    }

    @Override
    public void saveChatUser(@NotNull final ChatUser chatUser) {
        final var userFile = new File(usersDirectory, chatUser.uuid() + ".json");

        if (!userFile.exists()) {
            try {
                if (!userFile.createNewFile()) {
                    plugin.getLogger().log(
                        Level.WARNING,
                        "Something went wrong while creating user file. Could not save data for user: " +
                            chatUser.uuid()
                    );
                }
            } catch (final IOException exception) {
                plugin.getLogger().log(
                    Level.WARNING,
                    "Something went wrong while creating user file. Could not save data for user: " +
                        chatUser.uuid(),
                    exception
                );
            }
        }

        try (final var writer = new FileWriter(userFile)) {
            gson.toJson(chatUser, writer);
        } catch (final IOException exception) {
            plugin.getLogger().log(
                Level.WARNING,
                "Something went wrong while saving user file. Could not save data for user: " +
                    chatUser.uuid(),
                exception
            );
        }
    }

}
