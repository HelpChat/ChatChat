package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;

@Command(value = "togglemention", alias = "toggleping")
public class MentionToggleCommand extends BaseCommand {

    private static final String TOGGLE_PERMISSION = "chatchat.mentiontoggle";
    private final ChatChatPlugin plugin;

    public MentionToggleCommand(final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Permission(TOGGLE_PERMISSION)
    public void toggle(final ChatUser sender) {
        sender.mentions(!sender.mentions());

        final var messageHolder = plugin.configManager().messages();
        final var message = sender.mentions() ?
            messageHolder.mentionsEnabled() :
            messageHolder.mentionsDisabled();

        sender.sendMessage(message);
    }

}
