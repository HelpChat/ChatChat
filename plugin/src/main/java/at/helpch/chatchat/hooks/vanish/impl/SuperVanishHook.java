package at.helpch.chatchat.hooks.vanish.impl;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.hooks.vanish.VanishHook;
import de.myzelyam.api.vanish.VanishAPI;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 *     Vanish Hook that uses SuperVanish's implementation of player hiding
 *     {@link VanishAPI#canSee(Player, Player)} VanishAPI#canSee} to check if player is vanished.
 * </p>
 * <p>
 *     Some plugins that this is known to work with: SuperVanish, PremiumVanish.
 * </p>
 */
public class SuperVanishHook extends VanishHook {

    public SuperVanishHook(ChatChatPlugin ignoredPlugin) {}

    @Override
    public @NotNull Optional<String> dependency() {
        return Optional.of("SuperVanish");
    }

    @Override
    public void enable() {}

    @Override
    public boolean canSee(ChatUser user, ChatUser target) {
        return VanishAPI.canSee(user.player(), target.player());
    }
}
