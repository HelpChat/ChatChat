package at.helpch.chatchat.hooks.vanish;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.hook.VanishHook;
import at.helpch.chatchat.listener.EssentialsVanishListener;
import java.util.Optional;
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
public class EssentialsVanishHook extends VanishHook {

    private static final String ESSENTIALS = "Essentials";

    private final ChatChatPlugin plugin;

    public EssentialsVanishHook(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean register() {
        return Bukkit.getPluginManager().isPluginEnabled(ESSENTIALS);
    }

    @Override
    public @NotNull Optional<@NotNull String> name() {
        return Optional.of(ESSENTIALS);
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
