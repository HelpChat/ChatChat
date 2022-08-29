package at.helpch.chatchat.hooks.dsrv;

import at.helpch.chatchat.ChatChatAPIImpl;
import at.helpch.chatchat.api.event.ChatChatEvent;
import at.helpch.chatchat.user.ConsoleUser;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.hooks.chat.ChatHook;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class DsrvListener implements ChatHook {

    private final ChatChatAPIImpl api;

    public DsrvListener(@NotNull final ChatChatAPIImpl api) {
        this.api = api;
    }

    @Override
    public Plugin getPlugin() {
        return api.plugin();
    }

    @Override
    public void broadcastMessageToChannel(final String channelName, final Component component) {
        final var channel = api.plugin().configManager().channels().channels().get(channelName);
        if (channel == null) {
            api.plugin().getLogger().info("Couldn't find channel " + channelName);
            return;
        }
        // gotta love shading
        final var message = GsonComponentSerializer.gson().deserialize(
                github.scarsz.discordsrv.dependencies.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(component)
        );
        for (final var target : channel.targets(ConsoleUser.INSTANCE)) {
            target.sendMessage(message);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(ChatChatEvent event) {
        final var message = github.scarsz.discordsrv.dependencies.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().deserialize(
                GsonComponentSerializer.gson().serialize(event.message())
        );
        DiscordSRV.getPlugin().processChatMessage(event.user().player(), message,
                event.channel().name(), event.isCancelled());
    }
}
