package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.stream.Collectors;

@Command("ignorelist")
public class IgnoreListCommand extends BaseCommand {
    private final ChatChatPlugin plugin;
    private final static String IGNORELIST_PERMISSION = "chatchat.ignorelist";

    public IgnoreListCommand(final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Permission(IGNORELIST_PERMISSION)
    public void ignore(ChatUser sender) {
        final var ignoredUsers = new HashSet<>(sender.ignoredUsers());
        ignoredUsers.addAll(plugin.separationManager().separatedFrom(sender.uuid()));

        if (ignoredUsers.isEmpty()) {
            plugin.sendConfiguredMessage(sender, plugin.configManager().messages().notIgnoringAnyone());
            return;
        }

        String ignoredPlayers = ignoredUsers
            .stream()
            .map(Bukkit::getOfflinePlayer)
            .map(player -> player.getName() == null ? player.getUniqueId().toString() : player.getName())
            .collect(Collectors.joining(", "));

        sender.sendMessage(plugin.parseConfiguredMessage(sender, plugin.configManager().messages().ignoredPlayersList())
            .replaceText(builder -> builder.matchLiteral("<ignored_players>").replacement(ignoredPlayers)));
    }
}
