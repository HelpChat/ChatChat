package at.helpch.chatchat.hooks.vanish;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.hooks.AbstractInternalVanishHook;
import at.helpch.chatchat.listener.SuperVanishListener;
import de.myzelyam.api.vanish.VanishAPI;
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
public class SuperVanishHook extends AbstractInternalVanishHook {

    private static final String SUPER_VANISH = "SuperVanish";
    private static final String PREMIUM_VANISH = "PremiumVanish";

    private final ChatChatPlugin plugin;

    public SuperVanishHook(@NotNull final ChatChatPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        return plugin.configManager().extensions().addons().superVanishVanish() &&
            (Bukkit.getPluginManager().isPluginEnabled(SUPER_VANISH) || Bukkit.getPluginManager().isPluginEnabled(PREMIUM_VANISH));
    }

    @Override
    public @NotNull String name() {
        return (Bukkit.getPluginManager().isPluginEnabled(PREMIUM_VANISH) ? PREMIUM_VANISH : SUPER_VANISH) + "Hook";
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
