package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.event.PMSendEvent;
import at.helpch.chatchat.api.format.Format;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.util.FormatUtils;
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
    private final ChatChatPlugin plugin;
    private final boolean reply;

    public WhisperCommand(@NotNull final ChatChatPlugin plugin, final boolean reply) {
        this.plugin = plugin;
        this.reply = reply;
    }

    @Default
    @Permission(MESSAGE_PERMISSION)
    public void whisperCommand(
        final ChatUser sender,
        @Suggestion(value = "recipients") final ChatUser recipient,
        @Join final String message
    ) {
        if (!plugin.configManager().settings().privateMessagesSettings().enabled()) {
            sender.sendMessage(plugin.configManager().messages().unknownCommand());
            return;
        }

        if (!sender.privateMessages()) {
            sender.sendMessage(plugin.configManager().messages().repliesDisabled());
            return;
        }

        if (sender.equals(recipient)) {
            sender.sendMessage(plugin.configManager().messages().cantMessageYourself());
            return;
        }

        if (!sender.canSee(recipient) && !reply) {
            sender.sendMessage(plugin.configManager().messages().userOffline());
            return;
        }

        if (!recipient.privateMessages()) {
            sender.sendMessage(plugin.configManager().messages().targetRepliesDisabled());
            return;
        }

        if (recipient.ignoredUsers().contains(sender.uuid()) &&
            !sender.hasPermission(IgnoreCommand.IGNORE_BYPASS_PERMISSION)) {
            sender.sendMessage(plugin.configManager().messages().cantMessageGeneral());
            return;
        }

        if (sender.ignoredUsers().contains(recipient.uuid()) &&
            !recipient.hasPermission(IgnoreCommand.IGNORE_BYPASS_PERMISSION)) {
            sender.sendMessage(plugin.configManager().messages().cantMessageIgnoredPlayer());
            return;
        }

        if (message.isBlank()) {
            sender.sendMessage(plugin.configManager().messages().emptyMessage());
            return;
        }

        final var rulesResult = plugin.ruleManager().isAllowedPrivateChat(sender, recipient, message);
        if (rulesResult.isPresent()) {
            sender.sendMessage(rulesResult.get());
            return;
        }

        final var settingsConfig = plugin.configManager().settings();

        final var senderFormat = settingsConfig.privateMessagesSettings().formats().senderFormat();
        final var recipientFormat = settingsConfig.privateMessagesSettings().formats().recipientFormat();
        final var socialSpyFormat = settingsConfig.privateMessagesSettings().formats().socialSpyFormat();

        final var pmSendEvent = new PMSendEvent(
            sender,
            recipient,
            senderFormat,
            recipientFormat,
            Component.text(message),
            reply
        );

        plugin.getServer().getPluginManager().callEvent(pmSendEvent);

        if (pmSendEvent.isCancelled()) {
            return;
        }

        final var formats = new LinkedHashMap<Audience, Format>();
        formats.put(sender, pmSendEvent.senderFormat());
        formats.put(recipient, pmSendEvent.recipientFormat());
        formats.put(
            Audience.audience(
                plugin.usersHolder().users()
                    .stream()
                    .filter(spyUser -> !(spyUser instanceof ChatUser) || ((ChatUser) spyUser).socialSpy())
                    .filter(spyUser -> spyUser.uuid() != sender.uuid() && spyUser.uuid() != recipient.uuid())
                    .collect(Collectors.toUnmodifiableList())
            ),
            socialSpyFormat
        );

        formats.forEach((Audience audience, Format format) ->
            audience.sendMessage(FormatUtils.parseFormat(
                format,
                sender.player(),
                recipient.player(),
                pmSendEvent.message()
            ))
        );

        if (settingsConfig.mentions().privateMessage()) {
            recipient.playSound(settingsConfig.mentions().sound());
        }

        sender.lastMessagedUser(recipient);
        recipient.lastMessagedUser(sender);
    }

}
