package at.helpch.chatchat.hooks;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.hook.Hook;
import at.helpch.chatchat.api.hook.VanishHook;
import at.helpch.chatchat.hooks.dsrv.ChatChatDsrvHook;
import at.helpch.chatchat.hooks.towny.ChatChatTownyHook;
import at.helpch.chatchat.hooks.vanish.EssentialsVanishHook;
import at.helpch.chatchat.hooks.vanish.SuperVanishHook;
import at.helpch.chatchat.hooks.vanish.VanillaVanishHook;
import at.helpch.chatchat.util.Validators;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;

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
            registerHook(constructor);
        }
    }

    /**
     * Adds and registers a new {@link Hook} with the manager.
     * <br>
     * Registration will fail if the hook name is invalid (Check {@link Hook#name()} for valid hook name), if a hook
     * with the same name is already registered, if the hook throws an unhandled exception during registration or if the
     * hook returns false from {@link Hook#register()}.
     *
     * @param constructor A static method that takes in a {@link ChatChatPlugin} and returns a {@link Hook}. Preferably
     *                   a constructor.
     *
     * @return False if the registration failed, true otherwise.
     */
    public boolean addHook(@NotNull final Function<ChatChatPlugin, ? extends Hook> constructor) {
        return registerHook(constructor);
    }

    /**
     * Get all normal hooks.
     *
     * @return An unmodifiable {@link Set} of all {@link Hook}s.
     */
    public @NotNull Set<Hook> hooks() {
        return Collections.unmodifiableSet(hooks);
    }

    /**
     * Get all vanish hooks.
     *
     * @return An unmodifiable {@link Set} of all {@link VanishHook}s.
     */
    public @NotNull Set<VanishHook> vanishHooks() {
        return Collections.unmodifiableSet(vanishHooks);
    }

    private boolean registerHook(@NotNull final Function<ChatChatPlugin, ? extends Hook> constructor) {
        final Hook hook;

        try {
            hook = constructor.apply(plugin);
        } catch (final Throwable exception) { // Catching Throwable is a necessary evil to stop other hooks that don't
            // manage their own exceptions and just end up breaking our entire hook manager. This is a very common issue
            // with PlaceholderAPI.
            plugin.getLogger().log(
                Level.WARNING,
                "Failed to register hook " + constructor.getClass().getSimpleName() + "  because it threw an" +
                    " unhandled exception during construction. Please report this to the Hook author.",
                exception
            );
            return false;
        }

        if (hook == null) {
            plugin.getLogger().log(
                Level.WARNING,
                "Failed to register hook " + constructor.getClass().getSimpleName() + " because it returned null" +
                    " from its constructor. Please report this to the Hook author.");
            return false;
        }

        if (!Validators.isValidHookName(hook.name())) {
            plugin.getLogger().log(
                Level.WARNING,
                "Failed to register hook: " + hook.name() + " because it has an invalid name. Please report this" +
                    " to the Hook author."
            );
            return false;
        }

        try {
            if (!hook.register()) return false;

            if (hooks.stream().map(Hook::name).anyMatch(name -> name.equals(hook.name()))) {
                plugin.getLogger().log(
                    Level.WARNING,
                    "There was an attempt to register a duplicate hook: " + hook.name() +
                        ". Please report this to the Hook author."
                );
                return false;
            }

            hook.enable();

            final boolean result;
            if (hook instanceof VanishHook) {
                result = vanishHooks.add((VanishHook) hook);
            } else {
                result = hooks.add(hook);
            }

            plugin.getLogger().info("Enabled the " + hook.name() + " hook.");
            return result;
        } catch (final Throwable exception) { // Catching Throwable is a necessary evil to stop other hooks that don't
            // manage their own exceptions and just end up taking down our entire hook manager. This is a very common
            // issue with PlaceholderAPI.
            plugin.getLogger().log(
                Level.WARNING,
                "Failed to register hook " + hook.name() + "  because it threw an unhandled exception during" +
                    " registration. Please report this to the Hook author.",
                exception
            );
            return false;
        }
    }
}
