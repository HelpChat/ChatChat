package at.helpch.chatchat.format;

import at.helpch.chatchat.api.Format;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;

@ConfigSerializable
public final class ChatFormat implements Format {

    public static transient final ChatFormat DEFAULT_FORMAT = DefaultFormatFactory.createDefaultFormat();
    private int priority = Integer.MAX_VALUE;
    private List<String> parts = Collections.emptyList();

    // constructor for Configurate
    public ChatFormat() {}

    private ChatFormat(final int priority, @NotNull final List<String> parts) {
        this.priority = priority;
        this.parts = parts;
    }

    @Override
    public int priority() {
        return priority;
    }

    public @NotNull ChatFormat priority(final int priority) {
        return of(priority, parts);
    }

    @Override
    public @NotNull List<String> parts() {
        return parts;
    }

    public @NotNull ChatFormat parts(@NotNull final List<String> parts) {
        return of(priority, parts);
    }

    public static @NotNull ChatFormat of(final int priority, @NotNull final List<String> parts) {
        return new ChatFormat(priority, parts);
    }

    @Override
    public String toString() {
        return "ChatFormat{" +
                "priority=" + priority +
                ", parts=" + parts +
                '}';
    }
}
