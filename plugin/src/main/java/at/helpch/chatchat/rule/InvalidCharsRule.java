package at.helpch.chatchat.rule;

import at.helpch.chatchat.api.rule.Rule;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import org.jetbrains.annotations.NotNull;

public class InvalidCharsRule implements Rule {

    private static final String UTF_PERMISSION = "chatchat.utf";

    public boolean isAllowedPublic(@NotNull final ChatUser sender, @NotNull final String message) {
        return message.chars().noneMatch(it -> it > 127 && it != 248) || sender.hasPermission(UTF_PERMISSION);
    }

    public boolean isAllowedPrivate(@NotNull ChatUser sender, @NotNull User recipient, @NotNull String message) {
        return message.chars().noneMatch(it -> it > 127 && it != 248) || sender.hasPermission(UTF_PERMISSION);
    }

}
