package at.helpch.chatchat;

import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.User;
import at.helpch.chatchat.channel.ChannelTypeRegistry;
import at.helpch.chatchat.command.*;
import at.helpch.chatchat.config.ConfigManager;
import at.helpch.chatchat.hooks.HookManager;
import at.helpch.chatchat.listener.ChatListener;
import at.helpch.chatchat.listener.PlayerListener;
import at.helpch.chatchat.placeholder.ChatPlaceholders;
import at.helpch.chatchat.user.UserSenderValidator;
import at.helpch.chatchat.user.UsersHolder;
import dev.triumphteam.annotations.BukkitMain;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimpleBarChart;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@BukkitMain
public final class ChatChatPlugin extends JavaPlugin {

    private @NotNull
    final ConfigManager configManager = new ConfigManager(this, this.getDataFolder().toPath());
    private @NotNull
    final UsersHolder usersHolder = new UsersHolder();
    private @NotNull
    final ChannelTypeRegistry channelTypeRegistry = new ChannelTypeRegistry();
    private @NotNull
    final HookManager hookManager = new HookManager(this);
    private static BukkitAudiences audiences;
    private BukkitCommandManager<User> commandManager;

    @Override
    public void onEnable() {
        commandManager = BukkitCommandManager.create(this,
                usersHolder::getUser,
                new UserSenderValidator(this));

        audiences = BukkitAudiences.create(this);

        hookManager.init();
        configManager.reload();

        // bStats
        Metrics metrics = new Metrics(this, 14781);
        metrics.addCustomChart(new SimpleBarChart("channelTypes", () ->
                configManager().channels().channels().values().stream()
                        .collect(Collectors.toMap(s -> s.getClass().getName(), s -> 1, Integer::sum)))
        );

        registerSuggestions();
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

    public @NotNull BukkitCommandManager<User> commandManager() {
        return commandManager;
    }

    public @NotNull HookManager hookManager() {
        return hookManager;
    }

    private void registerSuggestions() {
        commandManager.registerSuggestion(SuggestionKey.of("recipients"), (sender, context) ->
                usersHolder.users()
                        .stream()
                        .filter(ChatUser.class::isInstance)
                        .map(ChatUser.class::cast)
                        .filter(sender::canSee)
                        .map(ChatUser::player)
                        .map(Player::getName)
                        .collect(Collectors.toUnmodifiableList())
        );
        commandManager.registerSuggestion(ChatUser.class, ((sender, context) -> Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList())));
    }

    private void registerCommands() {
        commandManager.registerArgument(ChatUser.class, (sender, arg) -> {
            final var player = Bukkit.getPlayer(arg);
            if (player == null) {
                return null;
            }
            return usersHolder.getUser(player);
        });

        List.of(
                new MainCommand(),
                new ReloadCommand(this),
                new WhisperCommand(this, false),
                new ReplyCommand(this, new WhisperCommand(this, true)),
                new WhisperToggleCommand(this),
                new SocialSpyCommand(this)
        ).forEach(commandManager::registerCommand);

        // register channel commands
        configManager.channels().channels().values().stream()
                .map(Channel::commandNames) // don't register empty command names
                .filter(s -> !s.isEmpty())
                .map(commandNames -> new SwitchChannelCommand(this, commandNames.remove(0), commandNames))
                .forEach(commandManager::registerCommand);
    }
}
