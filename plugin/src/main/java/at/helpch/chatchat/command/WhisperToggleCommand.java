package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import org.jetbrains.annotations.NotNull;

@Command(value = "togglemsg", alias = { "toggledms", "togglepms" })
public class WhisperToggleCommand extends BaseCommand {

    private static final String MESSAGE_TOGGLE_PERMISSION = "chatchat.pm.toggle";

    private final ChatChatPlugin plugin;

    public WhisperToggleCommand(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Permission(MESSAGE_TOGGLE_PERMISSION)
    public void whisperToggleCommand(final ChatUser user) {
        user.privateMessages(!user.privateMessages());

        if (user.privateMessages()) {
            user.sendMessage(plugin.configManager().messages().privateMessagesEnabled());
            return;
        }

        user.sendMessage(plugin.configManager().messages().privateMessagesDisabled());
    }
}
