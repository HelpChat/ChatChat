package at.helpch.chatchat.hooks;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatChatAPI;
import at.helpch.chatchat.hooks.dsrv.ChatChatDsrvHook;
import at.helpch.chatchat.hooks.gp.GriefPreventionSoftMuteHook;
import at.helpch.chatchat.hooks.towny.ChatChatTownyHook;
import at.helpch.chatchat.hooks.vanish.EssentialsVanishHook;
import at.helpch.chatchat.hooks.vanish.SuperVanishHook;
import at.helpch.chatchat.hooks.vanish.VanillaVanishHook;
import org.jetbrains.annotations.NotNull;

final class HookCreator {

    private final ChatChatPlugin plugin;

    public HookCreator(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    public @NotNull ChatChatDsrvHook createDsrvHook(@NotNull final ChatChatAPI ignoredApi) {
        return new ChatChatDsrvHook(plugin);
    }

    public @NotNull ChatChatTownyHook chatChatTownyHook(@NotNull final ChatChatAPI ignoredApi) {
        return new ChatChatTownyHook(plugin);
    }

    public @NotNull EssentialsVanishHook essentialsVanishHook(@NotNull final ChatChatAPI ignoredApi) {
        return new EssentialsVanishHook(plugin);
    }

    public @NotNull SuperVanishHook superVanishHook(@NotNull final ChatChatAPI ignoredApi) {
        return new SuperVanishHook(plugin);
    }

    public @NotNull VanillaVanishHook vanillaVanishHook(@NotNull final ChatChatAPI ignoredApi) {
        return new VanillaVanishHook(plugin);
    }

    public @NotNull GriefPreventionSoftMuteHook griefPreventionSoftMuteHook(@NotNull final ChatChatAPI ignoredApi) {
        return new GriefPreventionSoftMuteHook(plugin);
    }
}
