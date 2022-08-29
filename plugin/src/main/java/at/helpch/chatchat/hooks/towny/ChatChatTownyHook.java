package at.helpch.chatchat.hooks.towny;

import at.helpch.chatchat.ChatChatAPIImpl;
import at.helpch.chatchat.api.ChatChatAPI;
import at.helpch.chatchat.api.hook.Hook;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ChatChatTownyHook implements Hook {

    private static final String TOWNY = "Towny";

    private final ChatChatAPIImpl api;

    public ChatChatTownyHook(@NotNull final ChatChatAPI api) {
        if (!(api instanceof ChatChatAPIImpl)) {
            throw new IllegalArgumentException("api must be an instance of ChatChatAPIImpl");
        }

        this.api = (ChatChatAPIImpl) api;
    }

    @Override
    public boolean register() {
        return Bukkit.getPluginManager().isPluginEnabled(TOWNY);
    }

    @Override
    public @NotNull String name() {
        return "ChatChat:" + TOWNY + "Hook";
    }

    @Override
    public void enable() {
        api.channelTypeRegistry().add("TOWNY_TOWN", TownyTownChannel::new);
        api.channelTypeRegistry().add("TOWNY_NATION", TownyNationChannel::new);
    }
}
