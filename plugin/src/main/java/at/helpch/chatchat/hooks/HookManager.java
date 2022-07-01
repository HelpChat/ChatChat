package at.helpch.chatchat.hooks;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.hook.Hook;
import at.helpch.chatchat.hooks.dsrv.ChatChatDsrvHook;
import at.helpch.chatchat.hooks.towny.ChatChatTownyHook;
import at.helpch.chatchat.api.hook.VanishHook;
import at.helpch.chatchat.hooks.vanish.SuperVanishHook;
import at.helpch.chatchat.hooks.vanish.VanillaVanishHook;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public final class HookManager {
    private static final Set<Function<ChatChatPlugin, ? extends Hook>> constructors = Set.of(
        ChatChatDsrvHook::new,
        ChatChatTownyHook::new,
        VanillaVanishHook::new,
        SuperVanishHook::new
    );
    private final ChatChatPlugin plugin;
    private final Set<Hook> hooks = new HashSet<>();
    private final Set<VanishHook> vanishHooks = new HashSet<>();
    private boolean hasBeenInitialized = false;

    public HookManager(final @NotNull ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        if (hasBeenInitialized) {
            throw new IllegalStateException("Hook manager initialized twice");
        }
        hasBeenInitialized = true;

        for (final var constructor : constructors) {
            final var hook = constructor.apply(plugin);

            final @Nullable List<Plugin> hookPlugins = hook.dependency().isPresent()
                ? hook.dependency().get().stream()
                .map(Bukkit.getPluginManager()::getPlugin)
                .filter(Objects::nonNull)
                .filter(Plugin::isEnabled)
                .collect(Collectors.toUnmodifiableList())
                : Collections.emptyList();

            if (hook.dependency().isPresent() && hookPlugins.isEmpty()) {
                continue;
            }

            hook.enable();

            if (hook instanceof VanishHook) {
                vanishHooks.add((VanishHook) hook);
            } else {
                hooks.add(hook);
            }

            if (!hookPlugins.isEmpty()) {
                plugin.getLogger().info("Enabled " + hookPlugins.get(0).getName() + " hook.");
            }
        }
    }

    public @NotNull Set<Hook> hooks() {
        return hooks;
    }

    public @NotNull Set<VanishHook> vanishHooks() {
        return vanishHooks;
    }
}
