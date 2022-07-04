package at.helpch.chatchat.user;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.User;
import dev.triumphteam.cmd.core.SubCommand;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class UserSenderValidator implements SenderValidator<User> {
    private final ChatChatPlugin plugin;

    public UserSenderValidator(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Set<Class<? extends User>> getAllowedSenders() {
        return Set.of(User.class, ChatUser.class, ConsoleUser.class);
    }

    @Override
    public boolean validate(
            @NotNull final MessageRegistry<User> messageRegistry,
            @NotNull final SubCommand<User> subCommand,
            @NotNull final User sender
    ) {
        final var senderClass = subCommand.getSenderType();

        if (senderClass == ChatUser.class && !(sender instanceof ChatUser)) {
            sender.sendMessage(plugin.configManager().messages().playersOnly());
            return false;
        }

        if (senderClass == ConsoleUser.class && !(sender instanceof ConsoleUser)) {
            sender.sendMessage(plugin.configManager().messages().consoleOnly());
            return false;
        }

        return true;
    }
}
