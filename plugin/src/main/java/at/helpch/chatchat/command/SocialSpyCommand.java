package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Optional;
import org.jetbrains.annotations.NotNull;

@Command(value = "socialspy", alias = {"sspy", "pmspy", "spy"})
public final class SocialSpyCommand extends BaseCommand {
    private static final String MESSAGE_PERMISSION = "chatchat.socialspy";
    private final ChatChatPlugin plugin;

    public SocialSpyCommand(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Permission(MESSAGE_PERMISSION)
    public void socialSpy(final ChatUser user, @Optional final String toggle) {
        final boolean newState = toggle == null ?
                !user.socialSpy() :
                toggle.equals("true") || toggle.equals("yes") || toggle.equals("on") || toggle.equals("enable");

        user.socialSpy(newState);
        final var messages = plugin.configManager().messages();
        user.sendMessage(newState ? messages.socialSpyEnabled() : messages.socialSpyDisabled());
    }
}
