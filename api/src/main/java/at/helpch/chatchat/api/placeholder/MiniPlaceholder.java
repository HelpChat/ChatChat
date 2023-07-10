package at.helpch.chatchat.api.placeholder;

import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents a placeholder that can be used in MiniMessage.
 */
public interface MiniPlaceholder {

    /**
     * Compiles the placeholder to a {@link TagResolver}.
     * @param context The context in which the placeholder is used.
     * @return The compiled placeholder.
     */
    @NotNull TagResolver toTagResolver(final @NotNull Context context);

    public interface Context {

        /**
         * The response is going to be true only and only if the placeholder is used in a user generated message such as
         * a message sent in chat. If the placeholder is used in a format, or system message, the response is going to
         * be false.
         * @return Whether the placeholder is used in user sent message or in a format.
         */
        boolean inMessage();

        /**
         * The sender of the message is going to be empty if {@link Context#inMessage()} returns false.
         * @return the sender of the message
         */
        @NotNull Optional<ChatUser> sender();

        /**
         * The recipient can be a user that receives a system message or a player that receives a message from another
         * user.
         * The recipient is going to be empty if the message is not sent to a specific user.
         * @return The recipient of the message.
         */
        @NotNull Optional<User> recipient();
    }
}
