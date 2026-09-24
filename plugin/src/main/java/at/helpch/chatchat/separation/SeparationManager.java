package at.helpch.chatchat.separation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SeparationManager {

    private final Path file;
    private final Set<Pair> pairs = ConcurrentHashMap.newKeySet();

    public SeparationManager(@NotNull final Path dataFolder) throws IOException {
        this.file = dataFolder.resolve("separations.json");
        load();
    }

    public boolean isSeparated(@NotNull final UUID first, @NotNull final UUID second) {
        return pairs.contains(Pair.of(first, second));
    }

    public @NotNull Set<UUID> separatedFrom(@NotNull final UUID user) {
        final var result = new HashSet<UUID>();
        for (final var pair : pairs) {
            if (pair.first().equals(user)) result.add(pair.second());
            if (pair.second().equals(user)) result.add(pair.first());
        }
        return result;
    }

    public synchronized boolean separate(@NotNull final UUID first, @NotNull final UUID second) throws IOException {
        if (first.equals(second)) {
            throw new IllegalArgumentException("Cannot separate a player from themselves");
        }
        final var pair = Pair.of(first, second);
        if (!pairs.add(pair)) return false;

        try {
            save();
        } catch (final IOException exception) {
            pairs.remove(pair);
            throw exception;
        }
        return true;
    }

    public synchronized boolean unseparate(@NotNull final UUID first, @NotNull final UUID second) throws IOException {
        final var pair = Pair.of(first, second);
        if (!pairs.remove(pair)) return false;

        try {
            save();
        } catch (final IOException exception) {
            pairs.add(pair);
            throw exception;
        }
        return true;
    }

    private void load() throws IOException {
        if (Files.notExists(file)) return;

        try (final Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            final JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonArray()) throw new IOException("Invalid separations file: expected an array");

            for (final var entry : root.getAsJsonArray()) {
                if (!entry.isJsonArray() || entry.getAsJsonArray().size() != 2) {
                    throw new IOException("Invalid separation entry in " + file);
                }
                final var pair = entry.getAsJsonArray();
                final UUID first = UUID.fromString(pair.get(0).getAsString());
                final UUID second = UUID.fromString(pair.get(1).getAsString());
                if (first.equals(second)) throw new IOException("A player is separated from themselves in " + file);
                pairs.add(Pair.of(first, second));
            }
        } catch (final RuntimeException exception) {
            throw new IOException("Could not read " + file, exception);
        }
    }

    private void save() throws IOException {
        Files.createDirectories(file.getParent());
        final var array = new JsonArray();
        for (final var pair : pairs) {
            final var entry = new JsonArray();
            entry.add(pair.first().toString());
            entry.add(pair.second().toString());
            array.add(entry);
        }

        final var temporary = file.resolveSibling(file.getFileName() + ".tmp");
        Files.writeString(temporary, array.toString(), StandardCharsets.UTF_8);
        try {
            Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (final AtomicMoveNotSupportedException exception) {
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private record Pair(UUID first, UUID second) {
        private static Pair of(final UUID first, final UUID second) {
            return first.compareTo(second) <= 0 ? new Pair(first, second) : new Pair(second, first);
        }
    }
}
