package at.helpch.chatchat.hooks;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.hook.Hook;
import at.helpch.chatchat.hooks.dsrv.ChatChatDsrvHook;
import at.helpch.chatchat.hooks.towny.ChatChatTownyHook;
import at.helpch.chatchat.api.hook.VanishHook;
import at.helpch.chatchat.hooks.vanish.EssentialsVanishHook;
import at.helpch.chatchat.hooks.vanish.SuperVanishHook;
import at.helpch.chatchat.hooks.vanish.VanillaVanishHook;

import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public final class HookManager {
    private static final Set<Function<ChatChatPlugin, ? extends Hook>> constructors = Set.of(
        ChatChatDsrvHook::new,
        ChatChatTownyHook::new,
        VanillaVanishHook::new,
        EssentialsVanishHook::new,
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

            if (!hook.register()) return;
            hook.enable();

            if (hook instanceof VanishHook) {
                vanishHooks.add((VanishHook) hook);
            } else {
                hooks.add(hook);
            }

            if (hook.name().isPresent()) plugin.getLogger().info("Enabled the " + hook.name().get() + " hook.");
        }
    }

    public @NotNull Set<Hook> hooks() {
        return hooks;
    }

    public @NotNull Set<VanishHook> vanishHooks() {
        return vanishHooks;
    }
}
