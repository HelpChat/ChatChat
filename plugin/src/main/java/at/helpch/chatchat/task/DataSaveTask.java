package at.helpch.chatchat.task;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DataSaveTask extends BukkitRunnable {
    
    private final ChatChatPlugin plugin;
    
    public DataSaveTask(ChatChatPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final var user = plugin.usersHolder().getUser(player);

            if (!(user instanceof ChatUser)) continue;
            plugin.database().saveChatUser((ChatUser) user);
        }
    }
}
