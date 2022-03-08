package at.helpch.chatchat.user;

import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.User;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class ChatUser implements User {

    public ChatUser(@NotNull final UUID uuid) {
        this.uuid = uuid;
    }

    private @NotNull final UUID uuid;
    private Channel channel;

    @Override
    public @NotNull Channel channel() {
        return channel;
    }

    @Override
    public void channel(@NotNull final Channel channel) {
        this.channel = channel;
    }

    public @NotNull UUID uuid() {
        return uuid;
    }
}
