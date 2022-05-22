package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.Format;
import at.helpch.chatchat.api.event.PMSendEvent;
import at.helpch.chatchat.util.FormatUtils;
import at.helpch.chatchat.util.StringUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Join;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

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
    public void whisperCommand(
        final ChatUser user,
        @Suggestion(value = "recipients") final ChatUser recipient,
        @Join final String message
    ) {

        if (!user.privateMessages()) {
            user.sendMessage(plugin.configManager().messages().repliesDisabled());
            return;
        }

        if (user.equals(recipient)) {
            user.sendMessage(plugin.configManager().messages().cantMessageYourself());
            return;
        }

        // TODO: Somehow allow the /reply command work even if this condition is met but do not allow the /msg command.
        if (!user.canSee(recipient)) {
            user.sendMessage(plugin.configManager().messages().userOffline());
            return;
        }

        if (!recipient.privateMessages()) {
            user.sendMessage(plugin.configManager().messages().targetRepliesDisabled());
            return;
        }

        if (message.isBlank()) {
            user.sendMessage(plugin.configManager().messages().emptyMessage());
            return;
        }

        if (StringUtils.containsIllegalChars(message) && !user.player().hasPermission(UTF_PERMISSION)) {
            user.sendMessage(plugin.configManager().messages().specialCharactersNoPermission());
            return;
        }

        final var settingsConfig = plugin.configManager().settings();

        final var senderFormat = settingsConfig.senderFormat();
        final var recipientFormat = settingsConfig.recipientFormat();
        final var socialSpyFormat = settingsConfig.socialSpyFormat();

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

        Map.of(
                user, pmSendEvent.senderFormat(),
                recipient, pmSendEvent.recipientFormat(),
                Audience.audience(plugin.usersHolder().socialSpies()), socialSpyFormat
        ).forEach((Audience audience, Format format) ->
            audience.sendMessage(FormatUtils.parseFormat(
                    format,
                    user.player(),
                    recipient.player(),
                    pmSendEvent.message()
            ))
        );

        if (settingsConfig.mentionOnMessage()) {
            recipient.playSound(settingsConfig.mentionSound());
        }

        user.lastMessagedUser(recipient);
        recipient.lastMessagedUser(user);
    }
}
