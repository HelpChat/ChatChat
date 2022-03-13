package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.util.FormatUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Join;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Command(value = "reply", alias = "r")
public final class ReplyCommand extends BaseCommand {

    private static final String MESSAGE_PERMISSION = "chatchat.pm";
    private final ChatChatPlugin plugin;

    public ReplyCommand(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Permission(MESSAGE_PERMISSION)
    public void reply(final Player sender, @Join final String message) {
        final var user = plugin.usersHolder().getUser(sender);
        final var lastMessaged = user.lastMessagedUser();

        if (lastMessaged.isEmpty()) {
            plugin.audiences().player(sender).sendMessage(
                    Component.text("You have no one to reply to!", NamedTextColor.RED));
            return;
        }

        final var recipientUser = lastMessaged.get();
        final var recipient = recipientUser.player();
        if (!recipient.isOnline()) {
            plugin.audiences().player(sender).sendMessage(
                    Component.text("This player is no longer online", NamedTextColor.RED));
            return;
        }

        final var settingsConfig = plugin.configManager().settings();

        final var senderFormat = settingsConfig.getSenderFormat();
        final var recipientFormat = settingsConfig.getRecipientFormat();

        plugin.audiences().player(sender).sendMessage(FormatUtils.parseFormat(senderFormat, sender, recipient, message));
        plugin.audiences().player(recipient).sendMessage(FormatUtils.parseFormat(recipientFormat, sender, recipient, message));

        user.lastMessagedUser(recipientUser);
        recipientUser.lastMessagedUser(user);
    }
}
