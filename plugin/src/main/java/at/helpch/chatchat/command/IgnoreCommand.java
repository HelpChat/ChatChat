package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;

@Command("ignore")
public class IgnoreCommand extends BaseCommand {

    private final ChatChatPlugin plugin;
    private final static String IGNORE_PERMISSION = "chatchat.ignore";
    public static final String IGNORE_BYPASS_PERMISSION = IGNORE_PERMISSION + ".bypass";

    public IgnoreCommand(final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Permission(IGNORE_PERMISSION)
    @Default
    public void ignore(ChatUser sender, ChatUser target) {
        if (sender.uuid().equals(target.uuid())) {
            sender.sendMessage(plugin.configManager().messages().cantIgnoreYourself());
            return;
        }

        if (sender.ignoredUsers().contains(target.uuid())) {
            sender.sendMessage(plugin.configManager().messages().alreadyIgnored()
                .replaceText(builder -> builder.matchLiteral("<player>").replacement(target.player().getDisplayName())));
            return;
        }

        sender.ignoreUser(target);
        sender.sendMessage(plugin.configManager().messages().ignoredPlayer()
            .replaceText(builder -> builder.matchLiteral("<player>").replacement(target.player().getDisplayName())));
    }
}
