package at.helpch.chatchat.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Locale;

public final class PapiTagUtils {

    private PapiTagUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static @NotNull TagResolver createPlaceholderAPITag(final @Nullable OfflinePlayer player) {
        return createPlaceholderAPITag("papi", player);
    }

    public static @NotNull TagResolver createPlaceholderAPITag(
        final @NotNull String name,
        final @Nullable OfflinePlayer player
    ) {
        return TagResolver.resolver(name, (argumentQueue, context) -> {
            if (!argumentQueue.hasNext()) {
                return null;
            }

            final String next = argumentQueue.pop().value();

            final boolean inserting;
            final boolean append;
            switch (next.toLowerCase(Locale.ROOT)) {
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
            if (parsedPlaceholder.equals("%" + placeholder + '%')) {
                return null;
            }

            final var componentPlaceholder = LegacyComponentSerializer.legacySection().deserialize(
                ChatColor.translateAlternateColorCodes('&', parsedPlaceholder));

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

            final String next = argumentQueue.pop().value();

            final boolean inserting;
            final boolean append;
            switch (next.toLowerCase(Locale.ROOT)) {
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
            if (placeholder.isBlank() || !placeholder.contains("_") ||
                !placeholder.toLowerCase(Locale.ROOT).startsWith("rel_")) {
                return null;
            }

            final var parsedPlaceholder = PlaceholderAPI.setRelationalPlaceholders(
                player,
                target,
                '%' + placeholder + '%'
            );
            if (parsedPlaceholder.equals("%" + placeholder + '%')) {
                return null;
            }

            final var componentPlaceholder = LegacyComponentSerializer.legacySection().deserialize(
                ChatColor.translateAlternateColorCodes('&', parsedPlaceholder));

            return inserting ? Tag.inserting(componentPlaceholder) : Tag.selfClosingInserting(componentPlaceholder);
        });
    }

    public static @NotNull TagResolver createRecipientTag(@NotNull final Player player) {
        return createPlaceholderAPITag("recipient", player);
    }
}
