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
import java.nio.file.Files;
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
            return gson.fromJson(reader, ChatUser.class);
        } catch (final JsonParseException exception) { // Handles invalid JSON
            plugin.getLogger()
                    .log(
                        Level.WARNING,
                        String.format("Something went wrong while trying to load user %s!. Creating backup at %1$s-backup.json", uuid),
                        exception
                    );

            final var backupFile = new File(usersDirectory, uuid + "-backup.json");

            // Create the backup file if it does not exist.
            if (!backupFile.exists()) {
                try {
                    if (!backupFile.createNewFile()) {
                        plugin.getLogger().log(
                            Level.WARNING,
                            "Something went wrong while creating backup file. Shutting plugin down!"
                        );

                        plugin.getServer().getPluginManager().disablePlugin(plugin);
                    }

                } catch (final IOException ioEx) {
                    plugin.getLogger().log(
                        Level.WARNING,
                        "Something went wrong while creating backup file. Shutting plugin down!",
                        ioEx
                    );

                    plugin.getServer().getPluginManager().disablePlugin(plugin);
                }
            }

            // copy contents of userFile to backupFile
            try {
                Files.copy(userFile.toPath(), backupFile.toPath());
            } catch (IOException ioException) {
                plugin.getLogger().log(
                    Level.WARNING,
                    "Something went wrong while creating backup file. Shutting plugin down!",
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
