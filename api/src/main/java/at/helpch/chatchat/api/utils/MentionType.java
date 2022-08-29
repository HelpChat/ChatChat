package at.helpch.chatchat.api.utils;

import com.google.common.collect.Maps;

import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * <p>
 *     Represents the type of a mention. Currently mentions can be personal, where the sender directly mentions the
 *     target, or channel, where the sender mentions everyone that can see a channel and the target is included in that
 *     group.
 * </p>
 */
public enum MentionType {
    PERSONAL,
    CHANNEL,
    ;

    private static @NotNull final Map<@NotNull String, @NotNull MentionType> VALUES = Maps.newHashMap();

    /**
     * Get a mention type by its name. This is case-insensitive.
     *
     * @param name The name of the mention type.
     * @return The mention type with the given name.
     */
    public static @Nullable MentionType getType(@NotNull final String name) {
        return VALUES.get(name.toUpperCase(Locale.ROOT));
    }

    static {
        for (final MentionType type : values()) {
            VALUES.put(type.name(), type);
        }
    }
}
