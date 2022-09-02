package at.helpch.chatchat.api.placeholder;

import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a placeholder that can be used in MiniMessage.
 */
public interface MiniPlaceholder {
    /**
     * Compiles the placeholder into a {@link TagResolver}.
     *
     * @param inMessage whether the placeholder is used in user sent message or in a format.
     * @param sender the sender of the message.
     * @param recipient the recipient of the message.
     * @return the compiled placeholder.
     */
    @NotNull TagResolver toTagResolver(boolean inMessage, @NotNull ChatUser sender, @NotNull User recipient);
}
