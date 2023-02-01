package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

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
        if (sender.ignoredUsers().isEmpty()) {
            sender.sendMessage(plugin.configManager().messages().notIgnoringAnyone());
            return;
        }

        String ignoredPlayers = sender.ignoredUsers()
            .stream()
            .map(Bukkit::getOfflinePlayer)
            .map(OfflinePlayer::getName)
            .collect(Collectors.joining(", "));

        sender.sendMessage(plugin.configManager().messages().ignoredPlayersList()
            .replaceText(builder -> builder.matchLiteral("<ignored_players>").replacement(ignoredPlayers)));
    }
}
