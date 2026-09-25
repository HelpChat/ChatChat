package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.locale.LocaleMessage;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;

@Command("rangedchat")
public class RangedChatCommand extends BaseCommand {

    private static final String CHAT_TOGGLE_PERMISSION = "chatchat.rangedchat";
    private final ChatChatPlugin plugin;

    public RangedChatCommand(final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Permission(CHAT_TOGGLE_PERMISSION)
    public void toggleRangedChat(final ChatUser sender) {
        sender.rangedChat(!sender.rangedChat());

        final var message = sender.rangedChat() ?
            LocaleMessage.RANGED_CHAT_ENABLED_SUCCESSFULLY :
            LocaleMessage.RANGED_CHAT_DISABLED_SUCCESSFULLY;

        plugin.sendConfiguredMessage(sender, message);
    }
}
