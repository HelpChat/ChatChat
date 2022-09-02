package at.helpch.chatchat.rule;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.rule.Rule;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;

import java.util.Optional;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class InvalidCharsRule implements Rule {

    private static final String UTF_PERMISSION = "chatchat.utf";

    private final ChatChatPlugin plugin;

    public InvalidCharsRule(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isAllowedPublic(@NotNull final ChatUser sender, @NotNull final String message) {
        return message.chars().noneMatch(it -> it > 127 && it != 248) || sender.player().hasPermission(UTF_PERMISSION);
    }

    public boolean isAllowedPrivate(@NotNull ChatUser sender, @NotNull User receiver, @NotNull String message) {
        return message.chars().noneMatch(it -> it > 127 && it != 248) || sender.player().hasPermission(UTF_PERMISSION);
    }

    public @NotNull Optional<@NotNull Component> deniedMessage() {
        return Optional.of(plugin.configManager().messages().specialCharactersNoPermission());
    }
}
