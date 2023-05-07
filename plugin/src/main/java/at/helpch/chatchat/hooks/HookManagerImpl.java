package at.helpch.chatchat.hooks;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatChatAPI;
import at.helpch.chatchat.api.hook.Hook;
import at.helpch.chatchat.api.hook.HookManager;
import at.helpch.chatchat.api.hook.MuteHook;
import at.helpch.chatchat.api.hook.VanishHook;
import at.helpch.chatchat.api.utils.Validators;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;

public final class HookManagerImpl implements HookManager {

    private final ChatChatPlugin plugin;

    private final Set<Function<ChatChatAPI, ? extends Hook>> constructors = new HashSet<>();
    private final Set<VanishHook> vanishHooks = new HashSet<>();
    private final Set<MuteHook> muteHooks = new HashSet<>();
    private final Set<Hook> hooks = new HashSet<>();

    private boolean hasBeenInitialized = false;

    public HookManagerImpl(final @NotNull ChatChatPlugin plugin) {
        this.plugin = plugin;
        HookCreator hookCreator = new HookCreator(plugin);
        constructors.add(hookCreator::vanillaVanishHook);
        constructors.add(hookCreator::createDsrvHook);
        constructors.add(hookCreator::chatChatTownyHook);
        constructors.add(hookCreator::essentialsVanishHook);
        constructors.add(hookCreator::superVanishHook);
        constructors.add(hookCreator::griefPreventionSoftMuteHook);
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

    public boolean addHook(@NotNull final Function<ChatChatAPI, ? extends Hook> constructor) {
        return registerHook(constructor);
    }

    public @NotNull Set<Hook> hooks() {
        return Collections.unmodifiableSet(hooks);
    }

    public @NotNull Set<VanishHook> vanishHooks() {
        return Collections.unmodifiableSet(vanishHooks);
    }

    public @NotNull Set<MuteHook> muteHooks() {
        return Collections.unmodifiableSet(muteHooks);
    }

    private boolean registerHook(@NotNull final Function<ChatChatAPI, ? extends Hook> constructor) {
        final Hook hook;

        try {
            hook = constructor.apply(plugin.api());
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
                "Failed to register hook: " + hook.name() + ", from plugin: " + hook.plugin().getName()
                    + " because it has an invalid name. Please report this to the Hook authors: "
                    + String.join(", ", hook.plugin().getDescription().getAuthors())
            );
            return false;
        }

        try {
            if (!hook.register()) return false;

            if (hooks.stream().map(Hook::name).anyMatch(name -> name.equals(hook.name()))) {
                plugin.getLogger().log(
                    Level.WARNING,
                    hook.plugin().getName() + " attempted to register a hook with the name " + hook.name()
                        + " but a hook with that name is already registered. Please report this to the Hook authors: "
                        + String.join(", ", hook.plugin().getDescription().getAuthors())
                );
                return false;
            }

            hook.enable();

            final boolean result;
            if (hook instanceof MuteHook) {
                result = muteHooks.add((MuteHook) hook);
            } else if (hook instanceof VanishHook) {
                result = vanishHooks.add((VanishHook) hook);
            } else {
                result = hooks.add(hook);
            }

            plugin.getLogger().info("Enabled the " + hook.name() + " hook provided by: " + hook.plugin().getName()
                + ".");
            return result;
        } catch (final Throwable exception) { // Catching Throwable is a necessary evil to stop other hooks that don't
            // manage their own exceptions and just end up taking down our entire hook manager. This is a very common
            // issue with PlaceholderAPI.
            plugin.getLogger().log(
                Level.WARNING,
                "Failed to register hook: " + hook.name() + ", from plugin: " + hook.plugin().getName()
                    + " because it threw an unhandled exception during registration. Please report this to the Hook authors: "
                    + String.join(", ", hook.plugin().getDescription().getAuthors()),
                exception
            );
            return false;
        }
    }
}
