package at.helpch.chatchat.api.channel;

import at.helpch.chatchat.api.holder.FormatsHolder;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public interface Channel {

    @NotNull String name();

    @NotNull String messagePrefix();

    @NotNull String channelPrefix();

    @NotNull List<String> commandNames();

    @NotNull FormatsHolder formats();

    int radius();


    Set<User> targets(@NotNull final User source);

    boolean isUsableBy(@NotNull final ChatUser user);
}
