package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.event.PMSendEvent;
import at.helpch.chatchat.api.format.BasicFormat;
import at.helpch.chatchat.api.user.ChatUser;
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

import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Command(value = "whisper", alias = {"tell", "w", "msg", "message", "pm"})
public final class WhisperCommand extends BaseCommand {

    private static final String MESSAGE_PERMISSION = "chatchat.pm";
    private static final String UTF_PERMISSION = "chatchat.utf";
    private final ChatChatPlugin plugin;
    private final boolean reply;

    public WhisperCommand(@NotNull final ChatChatPlugin plugin, final boolean reply) {
        this.plugin = plugin;
        this.reply = reply;
    }

    @Default
    @Permission(MESSAGE_PERMISSION)
    public void whisperCommand(
        final ChatUser user,
        @Suggestion(value = "recipients") final ChatUser recipient,
        @Join final String message
    ) {
        if (!plugin.configManager().settings().privateMessagesSettings().enabled()) {
            user.sendMessage(plugin.configManager().messages().unknownCommand());
            return;
        }

        if (!user.privateMessages()) {
            user.sendMessage(plugin.configManager().messages().repliesDisabled());
            return;
        }

        if (user.equals(recipient)) {
            user.sendMessage(plugin.configManager().messages().cantMessageYourself());
            return;
        }

        if (!user.canSee(recipient) && !reply) {
            user.sendMessage(plugin.configManager().messages().userOffline());
            return;
        }

        if (!recipient.privateMessages()) {
            user.sendMessage(plugin.configManager().messages().targetRepliesDisabled());
            return;
        }

        final var userHasBypassPerm = user.player().hasPermission(IgnoreCommand.IGNORE_BYPASS_PERMISSION);
        if (recipient.ignoredUsers().contains(user.uuid()) && !userHasBypassPerm) {
            user.sendMessage(plugin.configManager().messages().cantMessageGeneral());
            return;
        }

        final var recipientHasBypassPerm = recipient.player().hasPermission(IgnoreCommand.IGNORE_BYPASS_PERMISSION);
        if (user.ignoredUsers().contains(recipient.uuid()) && !recipientHasBypassPerm) {
            user.sendMessage(plugin.configManager().messages().cantMessageIgnoredPlayer());
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

        final var senderFormat = settingsConfig.privateMessagesSettings().formats().senderFormat();
        final var recipientFormat = settingsConfig.privateMessagesSettings().formats().recipientFormat();
        final var socialSpyFormat = settingsConfig.privateMessagesSettings().formats().socialSpyFormat();

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

        final var formats = new LinkedHashMap<Audience, BasicFormat>();
        formats.put(user, pmSendEvent.senderFormat());
        formats.put(recipient, pmSendEvent.recipientFormat());
        formats.put(
            Audience.audience(
                plugin.usersHolder().users()
                    .stream()
                    .filter(spyUser -> !(spyUser instanceof ChatUser) || ((ChatUser) spyUser).socialSpy())
                    .collect(Collectors.toUnmodifiableList())
            ),
            socialSpyFormat
        );

        formats.forEach((Audience audience, BasicFormat format) ->
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
