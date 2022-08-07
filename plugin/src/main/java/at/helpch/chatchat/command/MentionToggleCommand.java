package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.util.MentionUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;

@Command(value = "togglemention", alias = "toggleping")
public class MentionToggleCommand extends BaseCommand {

    private final ChatChatPlugin plugin;

    public MentionToggleCommand(final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @SubCommand("personal")
    @Permission(MentionUtils.MENTION_PERSONAL_BLOCK_PERMISSION)
    public void togglePersonal(final ChatUser sender) {
        sender.personalMentions(!sender.personalMentions());

        final var messageHolder = plugin.configManager().messages();
        final var message = sender.personalMentions() ?
            messageHolder.personalMentionsEnabled() :
            messageHolder.personalMentionsDisabled();

        sender.sendMessage(message);
    }

    @SubCommand("channel")
    @Permission(MentionUtils.MENTION_CHANNEL_BLOCK_PERMISSION)
    public void toggleChannel(final ChatUser sender) {
        sender.channelMentions(!sender.channelMentions());

        final var messageHolder = plugin.configManager().messages();
        final var message = sender.channelMentions() ?
            messageHolder.channelMentionsEnabled() :
            messageHolder.channelMentionsDisabled();

        sender.sendMessage(message);
    }

}
