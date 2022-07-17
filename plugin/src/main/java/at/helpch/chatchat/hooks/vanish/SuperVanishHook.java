package at.helpch.chatchat.hooks.vanish;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.hook.VanishHook;
import at.helpch.chatchat.listener.SuperVanishListener;
import de.myzelyam.api.vanish.VanishAPI;
import java.util.Optional;
import org.bukkit.Bukkit;
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

    private static final String SUPER_VANISH = "SuperVanish";
    private static final String PREMIUM_VANISH = "PremiumVanish";

    private final ChatChatPlugin plugin;

    public SuperVanishHook(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        return Bukkit.getPluginManager().isPluginEnabled(SUPER_VANISH) ||
            Bukkit.getPluginManager().isPluginEnabled(PREMIUM_VANISH);
    }

    @Override
    public @NotNull Optional<@NotNull String> name() {
        if (Bukkit.getPluginManager().isPluginEnabled(SUPER_VANISH)) return Optional.of(SUPER_VANISH);
        if (Bukkit.getPluginManager().isPluginEnabled(PREMIUM_VANISH)) return Optional.of(PREMIUM_VANISH);
        return Optional.empty();
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
