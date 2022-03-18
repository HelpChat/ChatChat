package at.helpch.chatchat;

import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.command.MainCommand;
import at.helpch.chatchat.command.ReloadCommand;
import at.helpch.chatchat.command.ReplyCommand;
import at.helpch.chatchat.command.SwitchChannelCommand;
import at.helpch.chatchat.command.WhisperCommand;
import at.helpch.chatchat.config.ConfigManager;
import at.helpch.chatchat.listener.ChatListener;
import at.helpch.chatchat.listener.PlayerListener;
import at.helpch.chatchat.placeholder.ChatPlaceholders;
import at.helpch.chatchat.user.UsersHolder;
import dev.triumphteam.annotations.BukkitMain;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@BukkitMain
public final class ChatChatPlugin extends JavaPlugin {

    private @NotNull final ConfigManager configManager = new ConfigManager(this.getDataFolder().toPath());
    private @NotNull final UsersHolder usersHolder = new UsersHolder();
    private BukkitAudiences audiences;

    @Override
    public void onEnable() {
        configManager.reload();
        audiences = BukkitAudiences.create(this);

        registerCommands();

        // event listener registration
        List.of(
                new PlayerListener(this),
                new ChatListener(this)
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        new ChatPlaceholders(this).register();

        getLogger().info("Plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        audiences.close();

        getLogger().info("Plugin disabled successfully!");
    }

    public @NotNull ConfigManager configManager() {
        return configManager;
    }

    public @NotNull UsersHolder usersHolder() {
        return usersHolder;
    }

    public @NotNull BukkitAudiences audiences() {
        return audiences;
    }

    private void registerCommands() {
        final var commandManager = BukkitCommandManager.create(this);

        List.of(
                new MainCommand(this),
                new ReloadCommand(this),
                new WhisperCommand(this),
                new ReplyCommand(this)
        ).forEach(commandManager::registerCommand);

        // register channel commands
        configManager.channels().channels().values().stream()
                .filter(command -> !command.commandName().isEmpty()) // don't register empty command names
                .map(Channel::commandName)
                .map(command -> new SwitchChannelCommand(this, command))
                .forEach(commandManager::registerCommand);
    }
}
