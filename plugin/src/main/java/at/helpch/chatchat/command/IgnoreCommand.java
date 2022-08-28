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
    public static final String IGNORE_BYPASS_PERMISSION = IgnoreCommand.IGNORE_PERMISSION + ".bypass";

    public IgnoreCommand(final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Permission(IGNORE_PERMISSION)
    @Default
    public void ignore(ChatUser sender, ChatUser target) {
        final var ignored = sender.ignoredUsers().contains(target.uuid());

        final var messageHolder = plugin.configManager().messages();
        final var message = ignored ? messageHolder.unignoredPlayer() : messageHolder.ignoredPlayer();

        if (ignored) {
            sender.unignoreUser(target);
        } else {
            sender.ignoreUser(target);
        }

        sender.sendMessage(message
            .replaceText(builder -> builder.matchLiteral("<player>").replacement(target.player().getDisplayName())));
    }

}
