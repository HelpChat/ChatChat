package at.helpch.chatchat.format;

import at.helpch.chatchat.api.format.BasicFormat;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public final class BasicFormatImpl implements BasicFormat {

    private final String name;
    private final Map<String, List<String>> parts;

    public BasicFormatImpl(@NotNull final String name, @NotNull final Map<String, List<String>> parts) {
        this.name = name;
        this.parts = Collections.unmodifiableMap(parts);
    }

    @Override
    public @NotNull String name() {
        return name;
    }

    @Override
    public @NotNull BasicFormatImpl name(@NotNull String name) {
        return new BasicFormatImpl(name, parts);
    }

    @Override
    public @NotNull Map<String, List<String>> parts() {
        return parts;
    }

    @Override
    public @NotNull BasicFormatImpl parts(@NotNull final Map<String, List<String>> parts) {
        return new BasicFormatImpl(name, parts);
    }

    @Override
    public String toString() {
        return "BasicFormat{" +
            "name=" + name +
            ", parts=" + parts +
            '}';
    }
}
