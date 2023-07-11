package at.helpch.chatchat.placeholder;

import at.helpch.chatchat.api.placeholder.MiniPlaceholder;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MiniPlaceholderContext implements MiniPlaceholder.Context {

    private final boolean inMessage;
    private final @Nullable ChatUser sender;
    private final @Nullable User recipient;

    private MiniPlaceholderContext(final @NotNull Builder builder) {
        this.inMessage = builder.inMessage;
        this.sender = builder.sender;
        this.recipient = builder.recipient;
    }

    @Override
    public boolean inMessage() {
        return inMessage;
    }

    @Override
    public @NotNull Optional<ChatUser> sender() {
        return Optional.ofNullable(sender);
    }

    @Override
    public @NotNull Optional<User> recipient() {
        return Optional.ofNullable(recipient);
    }

    public @NotNull Builder toBuilder() {
        return new Builder(this);
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private boolean inMessage = false;
        private ChatUser sender = null;
        private User recipient = null;

        private Builder() {
        }

        private Builder(final @NotNull MiniPlaceholderContext context) {
            this.inMessage = context.inMessage;
            this.sender = context.sender;
            this.recipient = context.recipient;
        }

        public @NotNull Builder inMessage(final boolean inMessage) {
            this.inMessage = inMessage;
            return this;
        }

        public @NotNull Builder sender(final @Nullable ChatUser sender) {
            this.sender = sender;
            return this;
        }

        public @NotNull Builder recipient(final @Nullable User recipient) {
            this.recipient = recipient;
            return this;
        }

        public @NotNull MiniPlaceholderContext build() {
            return new MiniPlaceholderContext(this);
        }

    }

}
