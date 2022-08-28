package at.helpch.chatchat.api.utils;

import com.google.common.collect.Maps;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum MentionType {
    PERSONAL,
    CHANNEL,
    ;

    private static @NotNull final Map<@NotNull String, @NotNull MentionType> VALUES = Maps.newHashMap();

    public static @Nullable MentionType getType(@NotNull final String name) {
        return VALUES.get(name);
    }

    static {
        for (final MentionType type : values()) {
            VALUES.put(type.name(), type);
        }
    }
}
