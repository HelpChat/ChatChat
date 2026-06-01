package at.helpch.chatchat.cache;

import at.helpch.chatchat.ChatChatPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Stores remote reply targets so /reply can work for cross-server PMs.
 */
public final class RemoteReplyCache {

    private static final Map<UUID, ExpiringCache<String>> CACHE = new ConcurrentHashMap<>();

    private RemoteReplyCache() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static void remember(@NotNull final UUID userUuid, @NotNull final String targetName) {
        if (targetName.isBlank()) {
            return;
        }

        CACHE.computeIfAbsent(userUuid, ignored ->
            new ExpiringCache<>(ChatChatPlugin.cacheDuration(), TimeUnit.SECONDS)
        ).put(targetName);
    }

    public static @NotNull Optional<String> lastTarget(@NotNull final UUID userUuid) {
        return Optional.ofNullable(CACHE.get(userUuid)).flatMap(ExpiringCache::get);
    }

    public static void clear(@NotNull final UUID userUuid) {
        CACHE.remove(userUuid);
    }
}

