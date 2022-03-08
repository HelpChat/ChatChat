package at.helpch.chatchat.user;

import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.User;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class ChatUser implements User {

    public ChatUser(final @NotNull UUID uuid) {
        this.uuid = uuid;
    }

    private final @NotNull UUID uuid;
    private Channel channel;

    @Override
    @NotNull
    public Channel channel() {
        return channel;
    }

    @Override
    public void channel(@NotNull final Channel channel) {
        this.channel = channel;
    }

    @NotNull
    public UUID uuid() {
        return uuid;
    }
}
