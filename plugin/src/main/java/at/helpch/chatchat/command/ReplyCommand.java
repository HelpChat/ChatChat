package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Join;
import org.jetbrains.annotations.NotNull;

@Command(value = "reply", alias = "r")
public final class ReplyCommand extends BaseCommand {

    private static final String MESSAGE_PERMISSION = "chatchat.pm";
    private final ChatChatPlugin plugin;
    private final WhisperCommand whisperCommand;

    public ReplyCommand(@NotNull final ChatChatPlugin plugin, @NotNull final WhisperCommand whisperCommand) {
        this.plugin = plugin;
        this.whisperCommand = whisperCommand;
    }

    @Default
    @Permission(MESSAGE_PERMISSION)
    public void reply(final ChatUser user, @Join final String message) {
        final var lastMessaged = user.lastMessagedUser();

        if (lastMessaged.isEmpty()) {
            user.sendMessage(plugin.configManager().messages().noReplies());
            return;
        }

        whisperCommand.whisperCommand(user, lastMessaged.get(), message);
    }
}
