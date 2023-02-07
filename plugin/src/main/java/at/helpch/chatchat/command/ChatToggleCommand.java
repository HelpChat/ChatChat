package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;

@Command("togglechat")
public class ChatToggleCommand extends BaseCommand {

    private static final String CHAT_TOGGLE_PERMISSION = "chatchat.togglechat";
    private final ChatChatPlugin plugin;

    public ChatToggleCommand(final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Permission(CHAT_TOGGLE_PERMISSION)
    public void toggleChat(final ChatUser sender) {
        sender.chatState(!sender.chatEnabled());

        final var messageHolder = plugin.configManager().messages();
        final var message = sender.chatEnabled() ?
            messageHolder.chatEnabledSuccessfully() :
            messageHolder.chatDisabledSuccessfully();

        sender.sendMessage(message);
    }
}
