package at.helpch.chatchat.hooks.towny;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Hook;
import org.jetbrains.annotations.NotNull;

public class ChatChatTownyHook implements Hook {

    private final ChatChatPlugin plugin;

    public ChatChatTownyHook(ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String dependency() {
        return "Towny";
    }

    @Override
    public void enable() {
        plugin.channelTypeRegistry().add("TOWNY_TOWN", TownyTownChannel::new);
        plugin.channelTypeRegistry().add("TOWNY_NATION", TownyNationChannel::new);
    }
}
