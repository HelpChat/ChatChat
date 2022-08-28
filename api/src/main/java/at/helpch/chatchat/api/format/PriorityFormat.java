package at.helpch.chatchat.api.format;

import org.jetbrains.annotations.NotNull;

public interface PriorityFormat extends Format {

    int priority();

    @NotNull Format priority(final int priority);

}
