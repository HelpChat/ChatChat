package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.event.PMSendEvent;
import at.helpch.chatchat.util.FormatUtils;
import at.helpch.chatchat.util.StringUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Join;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

@Command(value = "whisper", alias = {"tell", "w", "msg", "message", "pm"})
public final class WhisperCommand extends BaseCommand {

    private static final String MESSAGE_PERMISSION = "chatchat.pm";
    private static final String UTF_PERMISSION = "chatchat.utf";
    private final ChatChatPlugin plugin;

    public WhisperCommand(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Permission(MESSAGE_PERMISSION)
    public void whisperCommand(final ChatUser user, final ChatUser recipient, @Join final String message) {

        if (user.equals(recipient)) {
            user.sendMessage(Component.text("You can't message yourself!", NamedTextColor.RED));
            return;
        }

        if (StringUtils.containsIllegalChars(message) && !user.player().hasPermission(UTF_PERMISSION)) {
            user.sendMessage(Component.text("You can't use special characters in chat!", NamedTextColor.RED));
            return;
        }

        final var settingsConfig = plugin.configManager().settings();

        final var senderFormat = settingsConfig.getSenderFormat();
        final var recipientFormat = settingsConfig.getRecipientFormat();

        final var pmSendEvent = new PMSendEvent(
            user,
            recipient,
            senderFormat,
            recipientFormat,
            Component.text(message),
            false
        );

        plugin.getServer().getPluginManager().callEvent(pmSendEvent);

        if (pmSendEvent.isCancelled()) {
            return;
        }

        user.sendMessage(FormatUtils.parseFormat(
            pmSendEvent.senderFormat(),
            user.player(),
            recipient.player(),
            pmSendEvent.message()
        ));
        recipient.sendMessage(FormatUtils.parseFormat(
            pmSendEvent.recipientFormat(),
            user.player(),
            recipient.player(),
            pmSendEvent.message()
        ));

        user.lastMessagedUser(recipient);
        recipient.lastMessagedUser(user);
    }
}
