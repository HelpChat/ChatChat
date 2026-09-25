package at.helpch.chatchat.util;

import at.helpch.chatchat.ChatChatPlugin;
import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentIteratorFlag;
import net.kyori.adventure.text.ComponentIteratorType;
import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/** Resolves components that need a Minecraft command source before being sent to a player. */
public final class ContextualComponents {

    private ContextualComponents() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static @NotNull Component resolve(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final Player sender,
        @NotNull final Component component
    ) {
        if (!needsResolution(component)) {
            return component;
        }

        try {
            if (Bukkit.isPrimaryThread()) {
                return PaperComponents.resolveWithContext(component, sender, sender);
            }
            return Bukkit.getScheduler().callSyncMethod(plugin,
                () -> PaperComponents.resolveWithContext(component, sender, sender)).get();
        } catch (final InterruptedException exception) {
            Thread.currentThread().interrupt();
            plugin.getLogger().warning("Interrupted while resolving a MiniMessage component: " + exception.getMessage());
        } catch (final IOException | ExecutionException exception) {
            plugin.getLogger().warning("Could not resolve a MiniMessage component: " + exception.getMessage());
        }
        return component;
    }

    static boolean needsResolution(@NotNull final Component component) {
        for (final var part : component.iterable(ComponentIteratorType.DEPTH_FIRST,
            ComponentIteratorFlag.INCLUDE_HOVER_SHOW_TEXT_COMPONENT,
            ComponentIteratorFlag.INCLUDE_HOVER_SHOW_ENTITY_NAME,
            ComponentIteratorFlag.INCLUDE_TRANSLATABLE_COMPONENT_ARGUMENTS)) {
            if (part instanceof SelectorComponent || part instanceof ScoreComponent || part instanceof NBTComponent<?, ?>) {
                return true;
            }
        }
        return false;
    }
}
