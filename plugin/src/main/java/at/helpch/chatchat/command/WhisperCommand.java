package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.event.PMSendEvent;
import at.helpch.chatchat.api.format.Format;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.cache.RemoteReplyCache;
import at.helpch.chatchat.cs.CrossServerPrivateMessageTracker;
import at.helpch.chatchat.cs.sender.RemoteMessageSender;
import at.helpch.chatchat.util.FormatUtils;
import at.helpch.chatchat.util.MessageUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Join;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        @Suggestion(value = "recipients") final String recipientName,
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

        if (message.isBlank()) {
            sender.sendMessage(plugin.configManager().messages().emptyMessage());
            return;
        }

        final var recipient = findLocalRecipient(recipientName);
        if (recipient == null) {
            sendCrossServerPrivateMessage(sender, recipientName, message);
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

        final var senderPlayer = sender.player();
        final var recipientPlayer = recipient.player();
        if (senderPlayer == null || recipientPlayer == null) {
            sender.sendMessage(plugin.configManager().messages().userOffline());
            return;
        }

        final var formats = new LinkedHashMap<Audience, Format>();
        formats.put(sender, pmSendEvent.senderFormat());
        formats.put(recipient, pmSendEvent.recipientFormat());
        formats.put(
            Audience.audience(
                plugin.usersHolder().users()
                    .stream()
                    .filter(spyUser -> !(spyUser instanceof ChatUser chatUser) || chatUser.socialSpy())
                    .filter(spyUser -> !spyUser.uuid().equals(sender.uuid()) && !spyUser.uuid().equals(recipient.uuid()))
                    .toList()
            ),
            socialSpyFormat
        );

        formats.forEach((Audience audience, Format format) ->
            audience.sendMessage(FormatUtils.parseFormat(
                format,
                senderPlayer,
                recipientPlayer,
                pmSendEvent.message()
            ))
        );

        if (settingsConfig.mentions().privateMessage()) {
            recipient.playSound(settingsConfig.mentions().sound());
        }

        sender.lastMessagedUser(recipient);
        recipient.lastMessagedUser(sender);
        RemoteReplyCache.remember(sender.uuid(), recipientPlayer.getName());
    }

    private void sendCrossServerPrivateMessage(
        @NotNull final ChatUser sender,
        @NotNull final String recipientName,
        @NotNull final String message
    ) {
        final var settingsConfig = plugin.configManager().settings();
        final var formats = settingsConfig.privateMessagesSettings().formats();
        final var rawMessage = Component.text(message);

        final var senderPlayer = sender.player();
        if (senderPlayer == null) {
            sender.sendMessage(plugin.configManager().messages().userOffline());
            return;
        }

        final var senderMessage = formatMessage(
            formats.senderFormat(),
            senderPlayer,
            senderPlayer.getName(),
            recipientName,
            rawMessage
        );

        final var recipientMessage = formatMessage(
            formats.recipientFormat(),
            senderPlayer,
            senderPlayer.getName(),
            recipientName,
            rawMessage
        );

        final var socialSpyMessage = formatMessage(
            formats.socialSpyFormat(),
            senderPlayer,
            senderPlayer.getName(),
            recipientName,
            rawMessage
        );

        final var requestId = UUID.randomUUID();
        CrossServerPrivateMessageTracker.registerPending(
            plugin,
            requestId,
            sender.uuid(),
            recipientName,
            MessageUtils.parseToGson(senderMessage),
            MessageUtils.parseToGson(socialSpyMessage)
        );

        final boolean sent = plugin.remoteMessageSender().sendPrivateMessage(new RemoteMessageSender.PrivateMessagePayload(
            requestId.toString(),
            sender.uuid().toString(),
            senderPlayer.getName(),
            recipientName,
            message,
            reply,
            sender.hasPermission(IgnoreCommand.IGNORE_BYPASS_PERMISSION),
            MessageUtils.parseToGson(recipientMessage),
            MessageUtils.parseToGson(socialSpyMessage)
        ));

        if (!sent) {
            CrossServerPrivateMessageTracker.cancelPending(requestId);
            sender.sendMessage(plugin.configManager().messages().userOffline());
        }
    }

    private @Nullable ChatUser findLocalRecipient(@NotNull final String recipientName) {
        return plugin.usersHolder().users()
            .stream()
            .filter(ChatUser.class::isInstance)
            .map(ChatUser.class::cast)
            .filter(user -> {
                final var player = user.player();
                return player != null && player.getName().equalsIgnoreCase(recipientName);
            })
            .findFirst()
            .orElse(null);
    }

    private @NotNull Component formatMessage(
        @NotNull final Format format,
        @Nullable final Player sender,
        @NotNull final String senderName,
        @NotNull final String recipientName,
        @NotNull final Component message
    ) {

        final var patchedFormat = patchNames(format, senderName, recipientName);
        if (sender != null) {
            return FormatUtils.parseFormat(patchedFormat, sender, message);
        }

        return FormatUtils.parseFormat(patchedFormat, message);
    }

    private @NotNull Format patchNames(
        @NotNull final Format format,
        @NotNull final String senderName,
        @NotNull final String recipientName
    ) {
        final Map<String, List<String>> parts = new LinkedHashMap<>();

        format.parts().forEach((key, value) -> {
            final var patchedEntries = new ArrayList<String>(value.size());
            value.forEach(entry -> patchedEntries.add(
                entry
                    .replace("%player_name%", senderName)
                    .replace("<recipient:player_name>", recipientName)
            ));
            parts.put(key, patchedEntries);
        });

        return format.parts(parts);
    }

}
