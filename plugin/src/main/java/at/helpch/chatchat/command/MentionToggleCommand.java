package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;

@Command(value = "togglemention", alias = "toggleping")
public class MentionToggleCommand extends BaseCommand {

    private static final String PERSONAL_TOGGLE_PERMISSION = "chatchat.mentiontoggle.personal";
    private static final String CHANNEL_TOGGLE_PERMISSION = "chatchat.mentiontoggle.channel";
    private final ChatChatPlugin plugin;

    public MentionToggleCommand(final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @SubCommand("personal")
    @Permission(PERSONAL_TOGGLE_PERMISSION)
    public void togglePersonal(final ChatUser sender) {
        sender.personalMentions(!sender.personalMentions());

        final var messageHolder = plugin.configManager().messages();
        final var message = sender.personalMentions() ?
            messageHolder.personalMentionsEnabled() :
            messageHolder.personalMentionsDisabled();

        sender.sendMessage(message);
    }

    @SubCommand("channel")
    @Permission(CHANNEL_TOGGLE_PERMISSION)
    public void toggleChannel(final ChatUser sender) {
        sender.channelMentions(!sender.channelMentions());

        final var messageHolder = plugin.configManager().messages();
        final var message = sender.channelMentions() ?
            messageHolder.channelMentionsEnabled() :
            messageHolder.channelMentionsDisabled();

        sender.sendMessage(message);
    }

}
