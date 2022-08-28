package at.helpch.chatchat.api.hook;

import at.helpch.chatchat.api.user.ChatUser;
import org.jetbrains.annotations.NotNull;

public abstract class VanishHook implements Hook {
    /**
     * Determines if a user can see another user.
     * @param user user for which to check if it can see the target.
     * @param target the target for which to check if its visible to the user.
     * @return true if the user can see the target, false otherwise.
     */
    public abstract boolean canSee(@NotNull final ChatUser user, @NotNull final ChatUser target);
}
