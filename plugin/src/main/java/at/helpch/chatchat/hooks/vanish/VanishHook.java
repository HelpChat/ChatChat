package at.helpch.chatchat.hooks.vanish;

import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.Hook;

public abstract class VanishHook implements Hook {
    /**
     * Determines if a user can see another user.
     * @param user user for which to check if it can see the target.
     * @param target the target for which to check if its visible to the user.
     * @return true if the user can see the target, false otherwise.
     */
    public abstract boolean canSee(ChatUser user, ChatUser target);
}
