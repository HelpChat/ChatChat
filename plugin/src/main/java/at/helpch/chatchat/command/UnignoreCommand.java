package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;

@Command("unignore")
public class UnignoreCommand extends BaseCommand {

    private final ChatChatPlugin plugin;
    private final static String IGNORE_PERMISSION = "chatchat.ignore";

    public UnignoreCommand(final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Permission(IGNORE_PERMISSION)
    @Default
    public void unignore(ChatUser sender, ChatUser target) {
        var targetPlayer = target.player();
        if (targetPlayer.isEmpty()) {
            plugin.sendConfiguredMessage(sender, plugin.configManager().messages().userOffline());
            return;
        }

        if (plugin.separationManager().isSeparated(sender.uuid(), target.uuid())) {
            sender.sendMessage(plugin.parseConfiguredMessage(sender, plugin.configManager().messages().separationLocked())
                .replaceText(builder -> builder.matchLiteral("<player>").replacement(targetPlayer.get().getDisplayName())));
            return;
        }

        if (!sender.ignoredUsers().contains(target.uuid())) {
            sender.sendMessage(plugin.parseConfiguredMessage(sender, plugin.configManager().messages().notIgnored())
                .replaceText(builder -> builder.matchLiteral("<player>").replacement(targetPlayer.get().getDisplayName())));
            return;
        }

        sender.unignoreUser(target);
        sender.sendMessage(plugin.parseConfiguredMessage(sender, plugin.configManager().messages().unignoredPlayer())
            .replaceText(builder -> builder.matchLiteral("<player>").replacement(targetPlayer.get().getDisplayName())));
    }
}
