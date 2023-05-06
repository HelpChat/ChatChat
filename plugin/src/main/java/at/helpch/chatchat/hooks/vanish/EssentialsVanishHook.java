package at.helpch.chatchat.hooks.vanish;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.hooks.AbstractInternalVanishHook;
import at.helpch.chatchat.listener.EssentialsVanishListener;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 *     Vanish Hook that uses Essentials' implementation of player hiding
 *    {@link net.ess3.api.IUser#setVanished(boolean) IUser#setVanished} to check if player is vanished.
 * </p>
 * <p>
 *     Some plugins that this is known to work with: EssentialsX
 * </p>
 */
public class EssentialsVanishHook extends AbstractInternalVanishHook {

    private static final String ESSENTIALS = "Essentials";

    private final ChatChatPlugin plugin;

    public EssentialsVanishHook(@NotNull final ChatChatPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        return plugin.configManager().extensions().addons().essentialsVanish() &&
            Bukkit.getPluginManager().isPluginEnabled(ESSENTIALS);
    }

    @Override
    public @NotNull String name() {
        return ESSENTIALS + "Hook";
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(new EssentialsVanishListener(plugin), plugin);
    }

    @Override
    public boolean canSee(@NotNull final ChatUser user, @NotNull final ChatUser target) {
        return user.player().canSee(target.player());
    }
}
