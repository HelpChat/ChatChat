package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.User;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;

@Command("separate")
public final class SeparateCommand extends BaseCommand {

    private static final String SEPARATE_PERMISSION = "chatchat.separate";
    private final ChatChatPlugin plugin;

    public SeparateCommand(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Permission(SEPARATE_PERMISSION)
    public void separate(final User sender,
                         @Suggestion("players") final String firstName,
                         @Suggestion("players") final String secondName) {
        final var first = SeparationCommandUtils.findPlayer(firstName);
        final var second = SeparationCommandUtils.findPlayer(secondName);
        if (first == null || second == null) {
            sender.sendMessage(plugin.configManager().messages().playerNotFound());
            return;
        }
        if (first.getUniqueId().equals(second.getUniqueId())) {
            sender.sendMessage(plugin.configManager().messages().cantSeparateSelf());
            return;
        }

        try {
            if (!plugin.separationManager().separate(first.getUniqueId(), second.getUniqueId())) {
                sender.sendMessage(plugin.configManager().messages().alreadySeparated());
                return;
            }
        } catch (final IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "Could not save player separation", exception);
            sender.sendMessage(plugin.configManager().messages().genericError());
            return;
        }

        sender.sendMessage(plugin.configManager().messages().separatedPlayers()
            .replaceText(builder -> builder.matchLiteral("<player1>")
                .replacement(SeparationCommandUtils.name(first, firstName)))
            .replaceText(builder -> builder.matchLiteral("<player2>")
                .replacement(SeparationCommandUtils.name(second, secondName))));
    }
}
