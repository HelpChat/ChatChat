package at.helpch.chatchat.data.base;

import at.helpch.chatchat.api.user.ChatUser;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public interface Database {
    public @NotNull ChatUser loadChatUser(@NotNull final UUID uuid);
    public void saveChatUser(@NotNull final ChatUser chatUser);
}
