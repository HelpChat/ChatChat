package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.format.PMFormat;
import at.helpch.chatchat.util.FormatUtils;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Command(value = "whisper", alias = {"tell", "w", "msg", "message"})
public final class WhisperCommand extends BaseCommand {

    private final ChatChatPlugin plugin;

    public WhisperCommand(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void whisperCommand(final Player sender, final Player target, final String message) {
        var settingsConfig = plugin.configManager().settings();

        final PMFormat senderFormat;
        final PMFormat receiverFormat;
        if (settingsConfig == null) {
            senderFormat = FormatUtils.createDefaultPrivateMessageSenderFormat();
            receiverFormat = FormatUtils.createDefaultPrivateMessageReceiverFormat();
        } else {
            senderFormat = settingsConfig.getSenderFormat();
            receiverFormat = settingsConfig.getRecieverFormat();
        }

        plugin.audiences().player(sender).sendMessage(FormatUtils.parseFormat(senderFormat, sender, message));
        plugin.audiences().player(target).sendMessage(FormatUtils.parseFormat(receiverFormat, target, message));
    }
}
