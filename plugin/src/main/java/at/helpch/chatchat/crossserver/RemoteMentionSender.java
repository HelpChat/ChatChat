package at.helpch.chatchat.crossserver;

import at.helpch.chatchat.api.user.User;
import at.helpch.chatchat.user.ChatUserImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.function.BooleanSupplier;

/** Sender context for mention checks when the player is connected to another backend. */
final class RemoteMentionSender extends ChatUserImpl {

    private final Set<String> permissions;
    private final BooleanSupplier canSeeTarget;
    private Boolean visible;

    RemoteMentionSender(@NotNull final UUID uuid, @NotNull final Set<String> permissions,
                        @NotNull final BooleanSupplier canSeeTarget) {
        super(uuid);
        this.permissions = Set.copyOf(permissions);
        this.canSeeTarget = canSeeTarget;
    }

    @Override
    public boolean hasPermission(@NotNull final String node) {
        return permissions.contains(node);
    }

    @Override
    public boolean canSee(@NotNull final User target) {
        if (visible == null) {
            visible = canSeeTarget.getAsBoolean();
        }
        return visible;
    }
}
