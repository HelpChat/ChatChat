package at.helpch.chatchat.user;

import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.User;
import org.jetbrains.annotations.NotNull;

public final class ChatUser implements User {

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
}
