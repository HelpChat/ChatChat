package at.helpch.chatchat.hooks.dsrv;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Hook;
import at.helpch.chatchat.api.event.ChatChatEvent;
import at.helpch.chatchat.user.ConsoleUser;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.hooks.chat.ChatHook;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class ChatChatDsrvHook implements ChatHook, Hook {

    private final ChatChatPlugin plugin;

    public ChatChatDsrvHook(final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String dependency() {
        return "DiscordSRV";
    }

    @Override
    public void enable() {
        DiscordSRV.getPlugin().getPluginHooks().add(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void broadcastMessageToChannel(final String channelName, final Component component) {
        final var channel = plugin.configManager().channels().channels().get(channelName);
        if (channel == null) {
            plugin.getLogger().info("Couldn't find channel " + channelName);
            return;
        }
        // gotta love shading
        final var message = GsonComponentSerializer.gson().deserialize(
                github.scarsz.discordsrv.dependencies.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(component)
        );
        // fixme - is it possible to actually find the sender?
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
