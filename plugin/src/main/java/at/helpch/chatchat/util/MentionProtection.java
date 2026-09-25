package at.helpch.chatchat.util;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/** Keeps generated text out of mention matching without changing what players see. */
final class MentionProtection {

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

    static @NotNull Component restore(@NotNull final Component component) {
        final var children = component.children().stream().map(MentionProtection::restore).toList();
        final var restored = component.children(children);
        if (!isProtected(component)) {
            return restored;
        }

        final var insertion = component.insertion().substring(MARKER.length());
        return restored.insertion(insertion.charAt(0) == '0' ? null : insertion.substring(1));
    }
}
