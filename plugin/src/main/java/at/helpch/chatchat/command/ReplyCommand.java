package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.cache.RemoteReplyCache;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Join;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Command(value = "reply", alias = "r")
public final class ReplyCommand extends BaseCommand {

    private static final String MESSAGE_PERMISSION = "chatchat.pm";
    private final ChatChatPlugin plugin;
    private final WhisperCommand whisperCommand;

    public ReplyCommand(@NotNull final ChatChatPlugin plugin, @NotNull final WhisperCommand whisperCommand) {
        this.plugin = plugin;
        this.whisperCommand = whisperCommand;
    }

    @Default
    @Permission(MESSAGE_PERMISSION)
    public void reply(final ChatUser user, @Join final String message) {
        if (!plugin.configManager().settings().privateMessagesSettings().enabled()) {
            user.sendMessage(plugin.configManager().messages().unknownCommand());
            return;
        }

        final var lastMessaged = user.lastMessagedUser();
        final var lastMessagedName = lastMessaged.flatMap(target ->
            Optional.ofNullable(target.player()).map(Player::getName)
        );

        if (lastMessagedName.isPresent()) {
            whisperCommand.whisperCommand(user, lastMessagedName.get(), message);
            return;
        }

        final var remoteTarget = RemoteReplyCache.lastTarget(user.uuid());
        if (remoteTarget.isPresent()) {
            whisperCommand.whisperCommand(user, remoteTarget.get(), message);
            return;
        }

        user.sendMessage(plugin.configManager().messages().noReplies());
    }
}
