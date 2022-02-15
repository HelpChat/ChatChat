package at.helpch.chatchat.api;

import org.jetbrains.annotations.NotNull;

public interface User {

    @NotNull
    Channel channel();

    void channel(@NotNull Channel channel);
}
