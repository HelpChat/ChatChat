package at.helpch.chatchat.api;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

public interface Channel extends Audience {

    @NotNull String name();

    @NotNull String messagePrefix();

    @NotNull String channelPrefix();

    @NotNull String commandName();

    boolean isUseableBy(ChatUser user);
}
