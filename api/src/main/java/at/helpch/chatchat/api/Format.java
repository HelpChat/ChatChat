package at.helpch.chatchat.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Format {

    int priority();

    @NotNull Format priority(final int priority);

    @NotNull List<String> parts();

    @NotNull Format parts(@NotNull final List<String> parts);
}
