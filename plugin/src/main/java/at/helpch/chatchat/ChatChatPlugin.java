package at.helpch.chatchat;

import at.helpch.chatchat.config.ConfigManager;
import dev.triumphteam.annotations.BukkitMain;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@BukkitMain
public final class ChatChatPlugin extends JavaPlugin {

    private final ConfigManager configManager = new ConfigManager(this.getDataFolder().toPath());

    @Override
    public void onEnable() {
    }

    @NotNull
    public ConfigManager configManager() {
        return configManager;
    }
}
