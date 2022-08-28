package at.helpch.chatchat.data.base;

import at.helpch.chatchat.api.user.ChatUser;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Database {
    public @NotNull ChatUser loadChatUser(@NotNull final UUID uuid);
    public void saveChatUser(@NotNull final ChatUser chatUser);
}
