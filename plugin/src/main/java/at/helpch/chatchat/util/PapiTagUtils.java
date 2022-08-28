package at.helpch.chatchat.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

public final class PapiTagUtils {

    private PapiTagUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static @NotNull TagResolver createPlaceholderAPITag(final @NotNull Player player) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            if (!argumentQueue.hasNext()) {
                return null;
            }

            final String next = argumentQueue.pop().value().toLowerCase(Locale.ROOT);
            if (!argumentQueue.hasNext()) {
                return null;
            }

            final boolean inserting;
            final boolean append;
            switch (next) {
                case "closing":
                    inserting = false;
                    append = false;
                    break;
                case "inserting":
                    inserting = true;
                    append = false;
                    break;
                default:
                    inserting = false;
                    append = true;
                    break;
            }

            final var arguments = new ArrayList<String>();
            if (append) {
                arguments.add(next);
            }

            while (argumentQueue.hasNext()) {
                arguments.add(argumentQueue.pop().value());
            }

            final var placeholder = String.join(":", arguments);
            if (placeholder.isBlank() || !placeholder.contains("_")) {
                return null;
            }

            final var parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + placeholder + '%');
            final var kyorifiedPlaceholder = Kyorifier.kyorify(parsedPlaceholder);
            final var componentPlaceholder = MessageUtils.parseToMiniMessage(kyorifiedPlaceholder);

            return inserting ? Tag.inserting(componentPlaceholder) : Tag.selfClosingInserting(componentPlaceholder);
        });
    }

    public static @NotNull TagResolver createRelPlaceholderAPITag(
        final @NotNull Player player,
        final @NotNull Player target
    ) {
        return TagResolver.resolver("papi-rel", (argumentQueue, context) -> {
            if (!argumentQueue.hasNext()) {
                return null;
            }

            final String next = argumentQueue.pop().value().toLowerCase(Locale.ROOT);
            if (!argumentQueue.hasNext()) {
                return null;
            }

            final boolean inserting;
            switch (next) {
                case "closing":
                    inserting = false;
                    break;
                case "inserting":
                    inserting = true;
                    break;
                default:
                    return null;
            }

            final var arguments = new ArrayList<String>();
            while (argumentQueue.hasNext()) {
                arguments.add(argumentQueue.pop().value());
            }

            final var placeholder = String.join(":", arguments);
            if (placeholder.isBlank() || !placeholder.contains("_") ||
                !placeholder.toLowerCase(Locale.ROOT).startsWith("rel_")) {
                return null;
            }

            final var parsedPlaceholder = PlaceholderAPI.setRelationalPlaceholders(
                player,
                target,
                '%' + placeholder + '%'
            );
            final var kyorifiedPlaceholder = Kyorifier.kyorify(parsedPlaceholder);
            final var componentPlaceholder = MessageUtils.parseToMiniMessage(kyorifiedPlaceholder);

            return inserting ? Tag.inserting(componentPlaceholder) : Tag.selfClosingInserting(componentPlaceholder);
        });
    }
}
