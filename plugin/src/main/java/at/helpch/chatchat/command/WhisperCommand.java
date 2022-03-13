package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.util.FormatUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Join;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Command(value = "whisper", alias = {"tell", "w", "msg", "message", "pm"})
public final class WhisperCommand extends BaseCommand {

    private static final String MESSAGE_PERMISSION = "chatchat.pm";
    private final ChatChatPlugin plugin;

    public WhisperCommand(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Permission(MESSAGE_PERMISSION)
    public void whisperCommand(final Player sender, @Suggestion("players") final Player recipient, @Join final String message) {

        if (sender.equals(recipient)) {
            plugin.audiences().player(sender).sendMessage(
                    Component.text("You can't message yourself!", NamedTextColor.RED));
            return;
        }

        final var settingsConfig = plugin.configManager().settings();

        final var senderFormat = settingsConfig.getSenderFormat();
        final var recipientFormat = settingsConfig.getRecipientFormat();

        plugin.audiences().player(sender).sendMessage(FormatUtils.parseFormat(
            senderFormat,
            sender,
            recipient,
            Component.text(message)
        ));
        plugin.audiences().player(recipient).sendMessage(FormatUtils.parseFormat(
            recipientFormat,
            sender,
            recipient,
            Component.text(message)
        ));

        final var usersHolder = plugin.usersHolder();
        final var user = usersHolder.getUser(sender);
        final var recipientUser = usersHolder.getUser(recipient);

        user.lastMessagedUser(recipientUser);
        recipientUser.lastMessagedUser(user);
    }
}
