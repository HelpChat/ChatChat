package at.helpch.chatchat.hooks.vanish;

import at.helpch.chatchat.ChatChatAPIImpl;
import at.helpch.chatchat.api.ChatChatAPI;
import at.helpch.chatchat.api.hook.VanishHook;
import at.helpch.chatchat.api.user.ChatUser;
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
public class SuperVanishHook extends VanishHook {

    private static final String SUPER_VANISH = "SuperVanish";
    private static final String PREMIUM_VANISH = "PremiumVanish";

    private final ChatChatAPIImpl api;

    public SuperVanishHook(@NotNull final ChatChatAPI api) {
        if (!(api instanceof ChatChatAPIImpl)) {
            throw new IllegalArgumentException("api must be an instance of ChatChatAPIImpl");
        }

        this.api = (ChatChatAPIImpl) api;
    }

    @Override
    public boolean register() {
        return Bukkit.getPluginManager().isPluginEnabled(SUPER_VANISH) ||
            Bukkit.getPluginManager().isPluginEnabled(PREMIUM_VANISH);
    }

    @Override
    public @NotNull String name() {
        return "ChatChat:" +
            (Bukkit.getPluginManager().isPluginEnabled(PREMIUM_VANISH) ? PREMIUM_VANISH : SUPER_VANISH) +
            "Hook";
    }

    @Override
    public void enable() {
        api.plugin().getServer().getPluginManager().registerEvents(new SuperVanishListener(api), api.plugin());
    }

    @Override
    public boolean canSee(@NotNull final ChatUser user, @NotNull final ChatUser target) {
        return VanishAPI.canSee(user.player(), target.player());
    }
}
