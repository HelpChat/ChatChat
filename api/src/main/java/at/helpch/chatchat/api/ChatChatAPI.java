package at.helpch.chatchat.api;

import at.helpch.chatchat.api.hook.HookManager;
import at.helpch.chatchat.api.user.UsersHolder;
import org.jetbrains.annotations.NotNull;

public interface ChatChatAPI {
    @NotNull UsersHolder usersHolder();

    @NotNull HookManager hookManager();
}
