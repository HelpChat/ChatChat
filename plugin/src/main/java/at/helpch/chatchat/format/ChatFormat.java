package at.helpch.chatchat.format;

import at.helpch.chatchat.api.format.PriorityFormat;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public final class ChatFormat implements PriorityFormat {

    private static PriorityFormat defaultFormat = DefaultFormatFactory.createDefaultFormat();
    private final String name;
    private final int priority;
    private final Map<String, List<String>> parts;

    public ChatFormat(@NotNull final String name, final int priority, @NotNull final Map<String, List<String>> parts) {
        this.name = name;
        this.priority = priority;
        this.parts = Collections.unmodifiableMap(parts);
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public @NotNull ChatFormat priority(final int priority) {
        return new ChatFormat(name, priority, parts);
    }

    @Override
    public @NotNull Map<String, List<String>> parts() {
        return parts;
    }

    @Override
    public @NotNull ChatFormat parts(@NotNull final Map<String, List<String>> parts) {
        return new ChatFormat(name, priority, parts);
    }

    public @NotNull String name() {
        return name;
    }

    public @NotNull ChatFormat name(@NotNull final String name) {
        return new ChatFormat(name, priority, parts);
    }

    public static @NotNull PriorityFormat defaultFormat() {
        return defaultFormat;
    }

    public static void defaultFormat(@NotNull final PriorityFormat format) {
        defaultFormat = format;
    }

    @Override
    public String toString() {
        return "ChatFormat{" +
                "name=" + name +
                ", priority=" + priority +
                ", parts=" + parts +
                '}';
    }
}
