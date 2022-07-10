package at.helpch.chatchat.api;

import org.jetbrains.annotations.NotNull;

public interface PriorityFormat extends Format {

    int priority();

    @NotNull Format priority(final int priority);
}