package at.helpch.chatchat.hooks.vanish;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.hook.VanishHook;
import at.helpch.chatchat.listener.VanillaVanishListener;
import java.util.List;
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

    private final ChatChatPlugin plugin;

    public VanillaVanishHook(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Optional<@NotNull List<String>> dependency() {
        return Optional.empty();
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(new VanillaVanishListener(plugin), plugin);
    }

    @Override
    public boolean canSee(@NotNull final ChatUser user, @NotNull final ChatUser target) {
        return user.player().canSee(target.player());
    }
}
