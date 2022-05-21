package at.helpch.chatchat.format;

import at.helpch.chatchat.api.Format;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public final class PMFormat implements Format {

    private final String name;
    private final Map<String, List<String>> parts;

    public PMFormat(@NotNull final String name, @NotNull final Map<String, List<String>> parts) {
        this.name = name;
        this.parts = Collections.unmodifiableMap(parts);
    }

    @Override
    public @NotNull String name() {
        return name;
    }

    @Override
    public @NotNull Format name(@NotNull String name) {
        return new PMFormat(name, parts);
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public @NotNull Format priority(final int priority) {
        return this;
    }

    @Override
    public @NotNull Map<String, List<String>> parts() {
        return parts;
    }

    @Override
    public @NotNull PMFormat parts(@NotNull final Map<String, List<String>> parts) {
        return new PMFormat(name, parts);
    }

    @Override
    public String toString() {
        return "PMFormat{" +
                "name=" + name +
                ", priority=" + priority() +
                ", parts=" + parts +
                '}';
    }
}
