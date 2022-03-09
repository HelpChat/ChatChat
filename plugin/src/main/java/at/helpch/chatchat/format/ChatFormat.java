package at.helpch.chatchat.format;

import at.helpch.chatchat.api.Format;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;

@ConfigSerializable
// configurate requires a 0 args constructor, so we use setters for now
public final class ChatFormat implements Format {

    private int priority = Integer.MAX_VALUE;
    private List<String> parts = Collections.emptyList();

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(final int priority) {
        this.priority = priority;
    }

    @Override
    public @NotNull List<String> getParts() {
        return parts;
    }

    public void setParts(@NotNull final List<String> parts) {
        this.parts = parts;
    }

    @Override
    public String toString() {
        return "ChatFormat{" +
                "priority=" + priority +
                ", parts=" + parts +
                '}';
    }
}
