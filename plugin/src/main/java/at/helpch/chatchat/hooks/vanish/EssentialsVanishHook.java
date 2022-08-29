package at.helpch.chatchat.hooks.vanish;

import at.helpch.chatchat.ChatChatAPIImpl;
import at.helpch.chatchat.api.ChatChatAPI;
import at.helpch.chatchat.api.hook.VanishHook;
import at.helpch.chatchat.api.user.ChatUser;
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
public class EssentialsVanishHook extends VanishHook {

    private static final String ESSENTIALS = "Essentials";

    private final ChatChatAPIImpl api;

    public EssentialsVanishHook(@NotNull final ChatChatAPI api) {
        if (!(api instanceof ChatChatAPIImpl)) {
            throw new IllegalArgumentException("api must be an instance of ChatChatAPIImpl");
        }

        this.api = (ChatChatAPIImpl) api;
    }

    @Override
    public boolean register() {
        return Bukkit.getPluginManager().isPluginEnabled(ESSENTIALS);
    }

    @Override
    public @NotNull String name() {
        return "ChatChat:" + ESSENTIALS + "Hook";
    }

    @Override
    public void enable() {
        api.plugin().getServer().getPluginManager().registerEvents(new EssentialsVanishListener(api), api.plugin());
    }

    @Override
    public boolean canSee(@NotNull final ChatUser user, @NotNull final ChatUser target) {
        return user.player().canSee(target.player());
    }
}
