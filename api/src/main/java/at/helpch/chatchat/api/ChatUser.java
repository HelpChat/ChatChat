package at.helpch.chatchat.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface ChatUser extends User {

    @NotNull Player player();

    @NotNull Optional<ChatUser> lastMessagedUser();

    void lastMessagedUser(@Nullable final ChatUser user);

    boolean privateMessages();

    void privateMessages(final boolean enable);

    boolean personalMentions();

    void personalMentions(final boolean receivesPersonalMentions);

    boolean channelMentions();

    void channelMentions(final boolean receivesChannelMentions);

    void socialSpy(final boolean enable);

    boolean socialSpy();

}
