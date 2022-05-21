package at.helpch.chatchat.hooks.vanish.impl;

import at.helpch.chatchat.ChatChatPlugin;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class PremiumVanishHook extends SuperVanishHook {

    public PremiumVanishHook(ChatChatPlugin plugin) {
        super(plugin);
    }

    @Override
    public @NotNull Optional<String> dependency() {
        return Optional.of("PremiumVanish");
    }
}