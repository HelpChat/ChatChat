package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.jetbrains.annotations.NotNull;

public class ServerListener implements Listener {

    private final ChatChatPlugin plugin;

    public ServerListener(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        plugin.hookManager().init();
    }
}
