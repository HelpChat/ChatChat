package at.helpch.chatchat.hooks;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Hook;
import at.helpch.chatchat.hooks.dsrv.ChatChatDsrvHook;
import at.helpch.chatchat.hooks.towny.ChatChatTownyHook;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public final class HookManager {
    private static final Set<Function<ChatChatPlugin, ? extends Hook>> constructors = Set.of(
            ChatChatDsrvHook::new,
            ChatChatTownyHook::new
    );
    private final ChatChatPlugin plugin;
    private final Set<Hook> hooks = new HashSet<>();
    private boolean hasBeenInitialized = false;

    public HookManager(final @NotNull ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        if (hasBeenInitialized) {
            throw new IllegalStateException("Hook manager initialized twice");
        }
        hasBeenInitialized = true;
        for (final var constructor: constructors) {
            final var hook = constructor.apply(plugin);
            final @Nullable var hookPlugin = Bukkit.getPluginManager().getPlugin(hook.dependency());
            if (hookPlugin == null || !hookPlugin.isEnabled()) continue;

            hook.enable();
            hooks.add(hook);
            plugin.getLogger().info("Enabled " + hook.dependency() + " hook");
        }
    }

    public @NotNull Set<Hook> hooks() {
        return hooks;
    }
}
