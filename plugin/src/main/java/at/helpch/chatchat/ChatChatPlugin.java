package at.helpch.chatchat;

import at.helpch.chatchat.api.ChatChatAPI;
import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.format.PriorityFormat;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import at.helpch.chatchat.channel.ChannelTypeRegistryImpl;
import at.helpch.chatchat.command.ChatToggleCommand;
import at.helpch.chatchat.command.DumpCommand;
import at.helpch.chatchat.command.FormatTestCommand;
import at.helpch.chatchat.command.IgnoreCommand;
import at.helpch.chatchat.command.IgnoreListCommand;
import at.helpch.chatchat.command.MainCommand;
import at.helpch.chatchat.command.MentionToggleCommand;
import at.helpch.chatchat.command.ReloadCommand;
import at.helpch.chatchat.command.ReplyCommand;
import at.helpch.chatchat.command.SocialSpyCommand;
import at.helpch.chatchat.command.SwitchChannelCommand;
import at.helpch.chatchat.command.UnignoreCommand;
import at.helpch.chatchat.command.WhisperCommand;
import at.helpch.chatchat.command.WhisperToggleCommand;
import at.helpch.chatchat.api.hook.Hook;
import at.helpch.chatchat.config.ConfigManager;
import at.helpch.chatchat.data.base.Database;
import at.helpch.chatchat.data.impl.gson.GsonDatabase;
import at.helpch.chatchat.hooks.HookManagerImpl;
import at.helpch.chatchat.listener.ChatListener;
import at.helpch.chatchat.listener.PlayerListener;
import at.helpch.chatchat.mention.MentionManagerImpl;
import at.helpch.chatchat.placeholder.MiniPlaceholderManagerImpl;
import at.helpch.chatchat.placeholder.PlaceholderAPIPlaceholders;
import at.helpch.chatchat.rule.RuleManagerImpl;
import at.helpch.chatchat.user.UserSenderValidator;
import at.helpch.chatchat.user.UsersHolderImpl;
import at.helpch.chatchat.util.DumpUtils;
import dev.triumphteam.annotations.BukkitMain;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimpleBarChart;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@BukkitMain
public final class ChatChatPlugin extends JavaPlugin {

    private @NotNull
    final ConfigManager configManager = new ConfigManager(this, this.getDataFolder().toPath());
    // We can move this inside onLoad or inside onEnable when we add different database types
    private @NotNull
    final Database database = new GsonDatabase(this);
    private @NotNull
    final UsersHolderImpl usersHolder = new UsersHolderImpl(this);
    private @NotNull
    final ChannelTypeRegistryImpl channelTypeRegistryImpl = new ChannelTypeRegistryImpl();
    private @NotNull
    final HookManagerImpl hookManager = new HookManagerImpl(this);
    private @NotNull
    final RuleManagerImpl ruleManager = new RuleManagerImpl(this);
    private @NotNull
    final MentionManagerImpl mentionsManager = new MentionManagerImpl(this);
    private @NotNull
    final MiniPlaceholderManagerImpl miniPlaceholdersManager = new MiniPlaceholderManagerImpl();
    private @NotNull
    final ChatChatAPIImpl api = new ChatChatAPIImpl(this);


    private static BukkitAudiences audiences;
    private BukkitCommandManager<User> commandManager;
    private BukkitTask dataSaveTask;

    private static long cacheDuration;

    @Override
    public void onLoad() {
        getServer().getServicesManager().register(ChatChatAPI.class, api, this, ServicePriority.Highest);
    }

    @Override
    public void onEnable() {
        audiences = BukkitAudiences.create(this);

        commandManager = BukkitCommandManager.create(this,
            usersHolder::getUser,
            new UserSenderValidator(this));

        configManager.reload();
        hookManager.init();

        // bStats
        Metrics metrics = new Metrics(this, 14781);
        metrics.addCustomChart(new SimpleBarChart("channelTypes", () ->
            configManager().channels().channels().values().stream()
                .collect(Collectors.toMap(s -> s.getClass().getName(), s -> 1, Integer::sum)))
        );

        registerSuggestions();
        registerArguments();
        registerCommandMessages();
        registerCommands();

        // event listener registration
        List.of(
            new PlayerListener(this),
            new ChatListener(this)
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        new PlaceholderAPIPlaceholders(this).register();

        cacheDuration = configManager().settings().lastMessagedCacheDuration();
        dataSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
            this,
            () -> Bukkit.getOnlinePlayers().stream().map(usersHolder::getUser).filter(user -> user instanceof ChatUser)
                .forEach(user -> database().saveChatUser((ChatUser) user)),
            20 * 60 * 5L,
            20 * 60 * 5L // Run the user save task every 5 minutes.
        );

        final int formats = configManager.formats().formats().size();
        final int channels = configManager.channels().channels().size();
        final int channelFormats = configManager.channels().channels().values().stream()
            .mapToInt(channel -> channel.formats().formats().size())
            .sum();

        getLogger().info("Plugin enabled successfully!");
        getLogger().info(formats + (formats == 1 ? " format" : " formats") + " loaded!");
        getLogger().info(channels + (channels == 1 ? " channel" : " channels") + " loaded!");
        getLogger().info(channelFormats + (channelFormats == 1 ? " channel format" : " channel formats") + " loaded!");
    }

    @Override
    public void onDisable() {
        hookManager().hooks().forEach(Hook::disable);
        hookManager().vanishHooks().forEach(Hook::disable);
        getServer().getServicesManager().unregisterAll(this);

        audiences.close();
        if (!dataSaveTask.isCancelled()) dataSaveTask.cancel();

        for (final Player player : Bukkit.getOnlinePlayers()) {
            usersHolder.removeUser(player);
        }

        getLogger().info("Plugin disabled successfully!");
    }

    public static long cacheDuration() {
        return cacheDuration;
    }

    public @NotNull ConfigManager configManager() {
        return configManager;
    }

    public @NotNull UsersHolderImpl usersHolder() {
        return usersHolder;
    }

    public @NotNull Database database() {
        return database;
    }

    public @NotNull ChannelTypeRegistryImpl channelTypeRegistry() {
        return channelTypeRegistryImpl;
    }

    public static @NotNull BukkitAudiences audiences() {
        return audiences;
    }

    public @NotNull BukkitCommandManager<User> commandManager() {
        return commandManager;
    }

    public @NotNull HookManagerImpl hookManager() {
        return hookManager;
    }

    public @NotNull RuleManagerImpl ruleManager() {
        return ruleManager;
    }

    public @NotNull MentionManagerImpl mentionsManager() {
        return mentionsManager;
    }

    public @NotNull MiniPlaceholderManagerImpl miniPlaceholdersManager() {
        return miniPlaceholdersManager;
    }

    public @NotNull ChatChatAPIImpl api() {
        return api;
    }

    private void registerArguments() {
        commandManager.registerArgument(PriorityFormat.class, (sender, argument) ->
            configManager().formats().formats().get(argument));

        commandManager.registerArgument(ChatUser.class, (sender, arg) -> {
            final var player = Bukkit.getPlayer(arg);
            if (player == null) {
                return null;
            }
            return usersHolder.getUser(player);
        });
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
        commandManager.registerSuggestion(SuggestionKey.of("files"), (sender, context) -> DumpUtils.FILES);
        commandManager.registerSuggestion(ChatUser.class, ((sender, context) -> Bukkit.getOnlinePlayers().stream()
            .map(Player::getName)
            .collect(Collectors.toList())));

        commandManager.registerSuggestion(PriorityFormat.class, ((sender, context) ->
            new ArrayList<>(configManager.formats().formats().keySet())
        ));
    }

    private void registerCommandMessages() {
        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) ->
            sender.sendMessage(configManager.messages().noPermission()));

        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) ->
            sender.sendMessage(configManager.messages().unknownCommand()));
        commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> {
            if (context.getArgumentType() == PriorityFormat.class) {
                sender.sendMessage(configManager.messages().invalidFormat());
                return;
            }

            if (context.getArgumentType() == ChatUser.class) {
                sender.sendMessage(configManager.messages().userOffline());
                return;
            }

            sender.sendMessage(configManager.messages().invalidArgument());
        });
        commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) ->
            sender.sendMessage(configManager.messages().invalidUsage()));
        commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) ->
            sender.sendMessage(configManager.messages().invalidUsage()));
    }

    private void registerCommands() {
        List.of(
            new MainCommand(),
            new IgnoreCommand(this),
            new UnignoreCommand(this),
            new IgnoreListCommand(this),
            new ReloadCommand(this),
            new MentionToggleCommand(this),
            new FormatTestCommand(this),
            new DumpCommand(this),
            new ChatToggleCommand(this)
        ).forEach(commandManager::registerCommand);

        if (configManager.settings().privateMessagesSettings().enabled()) {
            List.of(
                new WhisperCommand(this, false),
                new ReplyCommand(this, new WhisperCommand(this, true)),
                new WhisperToggleCommand(this),
                new SocialSpyCommand(this)
            ).forEach(commandManager::registerCommand);
        }

        // register channel commands
        configManager.channels().channels().values().stream()
            .map(Channel::commandNames) // don't register empty command names
            .filter(s -> !s.isEmpty())
            .map(commandNames -> new SwitchChannelCommand(this, commandNames.get(0), commandNames.subList(1, commandNames.size())))
            .forEach(commandManager::registerCommand);
    }
}
