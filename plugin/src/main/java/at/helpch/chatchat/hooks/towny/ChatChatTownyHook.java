package at.helpch.chatchat.hooks.towny;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.hook.Hook;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class ChatChatTownyHook implements Hook {

    private final ChatChatPlugin plugin;

    public ChatChatTownyHook(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Optional<@NotNull List<String>> dependency() {
        return Optional.of(List.of("Towny"));
    }

    @Override
    public void enable() {
        plugin.channelTypeRegistry().add("TOWNY_TOWN", TownyTownChannel::new);
        plugin.channelTypeRegistry().add("TOWNY_NATION", TownyNationChannel::new);
    }
}
