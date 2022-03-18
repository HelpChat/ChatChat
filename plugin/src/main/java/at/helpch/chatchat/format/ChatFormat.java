package at.helpch.chatchat.format;

import at.helpch.chatchat.api.Format;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public final class ChatFormat implements Format {

    private static ChatFormat defaultFormat = DefaultFormatFactory.createDefaultFormat();
    private final String name;
    private final int priority;
    private final List<String> parts;

    public ChatFormat(@NotNull final String name, final int priority, @NotNull final List<String> parts) {
        this.name = name;
        this.priority = priority;
        this.parts = parts;
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
    public @NotNull List<String> parts() {
        return List.copyOf(parts);
    }

    @Override
    public @NotNull ChatFormat parts(@NotNull final List<String> parts) {
        return new ChatFormat(name, priority, parts);
    }

    public @NotNull String name() {
        return name;
    }

    public @NotNull ChatFormat name(@NotNull final String name) {
        return new ChatFormat(name, priority, parts);
    }

    public static @NotNull ChatFormat defaultFormat() {
        return defaultFormat;
    }

    public static void defaultFormat(@NotNull final ChatFormat format) {
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
