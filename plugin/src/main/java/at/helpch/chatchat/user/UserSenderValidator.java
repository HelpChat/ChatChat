package at.helpch.chatchat.user;

import at.helpch.chatchat.api.User;
import dev.triumphteam.cmd.core.SubCommand;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class UserSenderValidator implements SenderValidator<User> {

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
            sender.sendMessage(Component.text("Player only."));
            return false;
        }

        if (senderClass == ConsoleUser.class && !(sender instanceof ConsoleUser)) {
            sender.sendMessage(Component.text("ur not console"));
            return false;
        }

        return true;
    }
}
