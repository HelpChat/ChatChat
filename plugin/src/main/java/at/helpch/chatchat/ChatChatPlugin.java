package at.helpch.chatchat;

import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.channel.ChannelTypeRegistry;
import at.helpch.chatchat.command.*;
import at.helpch.chatchat.config.ConfigManager;
import at.helpch.chatchat.listener.ChatListener;
import at.helpch.chatchat.listener.PlayerListener;
import at.helpch.chatchat.placeholder.ChatPlaceholders;
import at.helpch.chatchat.towny.TownyNationChannel;
import at.helpch.chatchat.towny.TownyTownChannel;
import at.helpch.chatchat.user.UserSenderValidator;
import at.helpch.chatchat.user.UsersHolder;
import dev.triumphteam.annotations.BukkitMain;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@BukkitMain
public final class ChatChatPlugin extends JavaPlugin {

    private @NotNull final ConfigManager configManager = new ConfigManager(this, this.getDataFolder().toPath());
    private @NotNull final UsersHolder usersHolder = new UsersHolder();
    private @NotNull final ChannelTypeRegistry channelTypeRegistry = new ChannelTypeRegistry();
    private static BukkitAudiences audiences;

    @Override
    public void onEnable() {
        audiences = BukkitAudiences.create(this);
        // fixme - move this somewhere else
        if (Bukkit.getPluginManager().getPlugin("Towny") != null) {
            channelTypeRegistry.add("TOWNY_TOWN", TownyTownChannel::new);
            channelTypeRegistry.add("TOWNY_NATION", TownyNationChannel::new);
        }
        configManager.reload();

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

    public @NotNull ChannelTypeRegistry channelTypeRegistry() {
        return channelTypeRegistry;
    }

    public static @NotNull BukkitAudiences audiences() {
        return audiences;
    }

    private void registerCommands() {
        final var commandManager = BukkitCommandManager.create(this,
                usersHolder::getUser,
                new UserSenderValidator());

        commandManager.registerArgument(ChatUser.class, (sender, arg) -> {
            final var player = Bukkit.getPlayer(arg);
            if (player == null) {
                return null;
            }
            return usersHolder.getUser(player);
        });
        commandManager.registerSuggestion(ChatUser.class, ((sender, context) -> Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList())));

        final var whisperCommand = new WhisperCommand(this);

        List.of(
                new MainCommand(this), // this causes tab complete errors? (default command)
                new ReloadCommand(this),
                whisperCommand,
                new ReplyCommand(whisperCommand),
                new SocialSpyCommand(this)
        ).forEach(commandManager::registerCommand);

        // register channel commands
        configManager.channels().channels().values().stream()
                .map(Channel::commandName) // don't register empty command names
                .filter(s -> !s.isEmpty())
                .map(command -> new SwitchChannelCommand(this, command))
                .forEach(commandManager::registerCommand);
    }
}
