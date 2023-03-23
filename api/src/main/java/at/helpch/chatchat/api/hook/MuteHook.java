package at.helpch.chatchat.api.hook;

import at.helpch.chatchat.api.user.ChatUser;
import org.jetbrains.annotations.NotNull;

/**
 * A hook that lets other plugins add their own mute integration with chat chat.
 */
public abstract class MuteHook implements Hook {
    /**
     * Determines if a user is muted.
     * @param user instance of a user to check if they are muted.
     * @return true if they are muted.
     */
    public abstract boolean isMuted(@NotNull ChatUser user);
}
