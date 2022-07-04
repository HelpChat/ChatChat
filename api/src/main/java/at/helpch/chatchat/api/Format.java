package at.helpch.chatchat.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface Format {

    @NotNull String name();

    @NotNull Format name(@NotNull final String name);

    int priority();

    @NotNull Format priority(final int priority);

    @NotNull Map<String, List<String>> parts();

    @NotNull Format parts(@NotNull final Map<String, List<String>> parts);
}
