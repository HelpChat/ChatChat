package at.helpch.chatchat.hooks.vanish;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.hook.VanishHook;
import at.helpch.chatchat.listener.SuperVanishListener;
import de.myzelyam.api.vanish.VanishAPI;
import java.util.List;
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

    private final ChatChatPlugin plugin;

    public SuperVanishHook(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Optional<@NotNull List<String>> dependency() {
        return Optional.of(List.of("SuperVanish", "PremiumVanish"));
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(new SuperVanishListener(plugin), plugin);
    }

    @Override
    public boolean canSee(@NotNull final ChatUser user, @NotNull final ChatUser target) {
        return VanishAPI.canSee(user.player(), target.player());
    }
}
