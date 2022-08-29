package at.helpch.chatchat;

import at.helpch.chatchat.api.ChatChatAPI;
import at.helpch.chatchat.api.user.UsersHolder;
import org.jetbrains.annotations.NotNull;

public class ChatChatAPIImpl implements ChatChatAPI {

    private final ChatChatPlugin plugin;

    public ChatChatAPIImpl(ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    public @NotNull UsersHolder usersHolder() {
        return plugin.usersHolder();
    }

    public @NotNull ChatChatPlugin plugin() {
        return plugin;
    }
}
