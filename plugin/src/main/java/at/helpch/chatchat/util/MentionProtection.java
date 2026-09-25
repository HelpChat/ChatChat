package at.helpch.chatchat.util;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/** Keeps generated text out of mention matching without changing what players see. */
public final class MentionProtection {

    private static final String MARKER = "\u0000chatchat:item:" + UUID.randomUUID() + ":";

    private MentionProtection() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    static @NotNull Component protect(@NotNull final Component component) {
        final var children = component.children().stream().map(MentionProtection::protect).toList();
        final var insertion = component.insertion();
        return component.children(children).insertion(MARKER + (insertion == null ? "0" : "1" + insertion));
    }

    static boolean isProtected(@NotNull final Component component) {
        final var insertion = component.insertion();
        return insertion != null && insertion.startsWith(MARKER);
    }

    public static @NotNull String transportMarker() {
        return MARKER;
    }

    /** Transfer protected item components from another server's marker to this server's marker. */
    public static @NotNull Component rebase(@NotNull final Component component,
                                            @NotNull final String foreignMarker) {
        final var children = component.children().stream()
            .map(child -> rebase(child, foreignMarker)).toList();
        final var rebased = component.children(children);
        final var insertion = component.insertion();
        if (!foreignMarker.startsWith("\u0000chatchat:item:") || insertion == null ||
            !insertion.startsWith(foreignMarker)) {
            return rebased;
        }
        return rebased.insertion(MARKER + insertion.substring(foreignMarker.length()));
    }

    public static @NotNull Component restore(@NotNull final Component component) {
        final var children = component.children().stream().map(MentionProtection::restore).toList();
        final var restored = component.children(children);
        if (!isProtected(component)) {
            return restored;
        }

        final var insertion = component.insertion().substring(MARKER.length());
        return restored.insertion(insertion.charAt(0) == '0' ? null : insertion.substring(1));
    }
}
