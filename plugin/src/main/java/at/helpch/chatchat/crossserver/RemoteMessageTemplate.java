package at.helpch.chatchat.crossserver;

import at.helpch.chatchat.api.mention.MentionResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Function;

/** Finds the message body within a formatted component without matching names or prefixes. */
final class RemoteMessageTemplate {

    private RemoteMessageTemplate() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    static @NotNull Rendered process(@NotNull final Component component, @NotNull final String marker,
                                     @NotNull final Function<Component, MentionResult> processor) {
        if (marker.equals(component.insertion())) {
            final var result = processor.apply(component.insertion(null));
            return new Rendered(result.message(), result.playSound());
        }

        final var children = new ArrayList<Component>(component.children().size());
        var playSound = false;
        for (final var child : component.children()) {
            final var rendered = process(child, marker, processor);
            children.add(rendered.component());
            playSound |= rendered.playSound();
        }
        return new Rendered(component.children(children), playSound);
    }

    static @NotNull Component strip(@NotNull final Component component, @NotNull final String marker) {
        final var children = component.children().stream().map(child -> strip(child, marker)).toList();
        final var stripped = component.children(children);
        return marker.equals(component.insertion()) ? stripped.insertion(null) : stripped;
    }

    record Rendered(Component component, boolean playSound) {
    }
}
