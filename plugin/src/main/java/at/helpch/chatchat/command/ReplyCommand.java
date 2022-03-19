package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.event.PMSendEvent;
import at.helpch.chatchat.util.FormatUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Join;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    public void reply(final ChatUser user, @Join final String message) {
        final var lastMessaged = user.lastMessagedUser();

        if (lastMessaged.isEmpty()) {
            user.sendMessage(Component.text("You have no one to reply to!", NamedTextColor.RED));
            return;
        }

        final var recipientUser = lastMessaged.get();
        final var recipient = recipientUser.player();
        if (!recipient.isOnline()) {
            user.sendMessage(Component.text("This player is no longer online", NamedTextColor.RED));
            return;
        }

        final var settingsConfig = plugin.configManager().settings();

        final var senderFormat = settingsConfig.getSenderFormat();
        final var recipientFormat = settingsConfig.getRecipientFormat();

        final var pmSendEvent = new PMSendEvent(
            user.player(),
            recipient,
            senderFormat,
            recipientFormat,
            Component.text(message),
            true
        );

        plugin.getServer().getPluginManager().callEvent(pmSendEvent);

        if (pmSendEvent.isCancelled()) {
            return;
        }

        user.sendMessage(FormatUtils.parseFormat(
            pmSendEvent.senderFormat(),
            user.player(),
            recipient,
            pmSendEvent.message()
        ));
        recipientUser.sendMessage(FormatUtils.parseFormat(
            pmSendEvent.recipientFormat(),
            user.player(),
            recipient,
            pmSendEvent.message()
        ));

        user.lastMessagedUser(recipientUser);
        recipientUser.lastMessagedUser(user);
    }
}
