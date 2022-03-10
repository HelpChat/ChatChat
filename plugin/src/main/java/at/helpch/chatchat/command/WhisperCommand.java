package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.util.FormatUtils;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Command("chatchat")
public class WhisperCommand extends BaseCommand {

    private final ChatChatPlugin plugin;

    public WhisperCommand(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @SubCommand(value = "whisper", alias = {"tell", "w", "msg"})
    public void whisperCommand(final Player sender, final Player target, final String message) {
        var settingsConfig = plugin.configManager().settings();
        var senderFormat = settingsConfig.getSenderFormat();
        var recieverFormat = settingsConfig.getRecieverFormat();

        plugin.audiences().player(sender).sendMessage(FormatUtils.parseFormat(senderFormat, sender, message));
        plugin.audiences().player(target).sendMessage(FormatUtils.parseFormat(recieverFormat, target, message));
    }
}
