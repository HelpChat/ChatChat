package at.helpch.chatchat.hooks.vanish.impl;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.hooks.vanish.VanishHook;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 *     Vanish Hook that uses spigot's default implementation of player hiding
 *     {@link org.bukkit.entity.Player#hidePlayer(Plugin, Player) Player#hidePlayer} to check if player is vanished.
 * </p>
 * <p>
 *     Some plugins that this is known to work with: EssentialsX.
 * </p>
 */
public class VanillaVanishHook extends VanishHook {

    public VanillaVanishHook(ChatChatPlugin ignoredPlugin) {}

    @Override
    public @NotNull Optional<String> dependency() {
        return Optional.empty();
    }

    @Override
    public void enable() {}

    @Override
    public boolean canSee(ChatUser user, ChatUser target) {
        return user.player().canSee(target.player());
    }
}
