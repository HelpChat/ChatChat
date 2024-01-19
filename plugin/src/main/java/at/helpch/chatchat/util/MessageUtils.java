package at.helpch.chatchat.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class MessageUtils {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    private MessageUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static @NotNull Component parseFromGson(@NotNull final String formatPart) {
        return GsonComponentSerializer.gson().deserialize(formatPart);
    }

    public static @NotNull String parseToGson(@NotNull final ComponentLike component) {
        return GsonComponentSerializer.gson().serialize(component.asComponent());
    }

    public static @NotNull Component parseFromMiniMessage(@NotNull final String formatPart) {
        return miniMessage.deserialize(formatPart);
    }

    public static @NotNull Component parseFromMiniMessage(@NotNull final String formatPart, @NotNull final TagResolver tag) {
        return miniMessage.deserialize(formatPart, tag);
    }

    public static @NotNull Component parseFromMiniMessage(@NotNull final String formatPart, @NotNull final TagResolver... tags) {
        return miniMessage.deserialize(formatPart, tags);
    }

    public static @NotNull Component parseFromMiniMessage(@NotNull final String formatPart, @NotNull final List<TagResolver> tags) {
        return miniMessage.deserialize(formatPart, TagResolver.resolver(tags));
    }

}
