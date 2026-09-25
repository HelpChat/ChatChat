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

@Command("unseparate")
public final class UnseparateCommand extends BaseCommand {

    private static final String UNSEPARATE_PERMISSION = "chatchat.unseparate";
    private final ChatChatPlugin plugin;

    public UnseparateCommand(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Permission(UNSEPARATE_PERMISSION)
    public void unseparate(final User sender,
                           @Suggestion("players") final String firstName,
                           @Suggestion("players") final String secondName) {
        final var first = SeparationCommandUtils.findPlayer(firstName);
        final var second = SeparationCommandUtils.findPlayer(secondName);
        if (first == null || second == null) {
            plugin.sendConfiguredMessage(sender, plugin.configManager().messages().playerNotFound());
            return;
        }
        if (first.getUniqueId().equals(second.getUniqueId())) {
            plugin.sendConfiguredMessage(sender, plugin.configManager().messages().cantSeparateSelf());
            return;
        }

        try {
            if (!plugin.separationManager().unseparate(first.getUniqueId(), second.getUniqueId())) {
                plugin.sendConfiguredMessage(sender, plugin.configManager().messages().notSeparated());
                return;
            }
        } catch (final IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "Could not save player separation", exception);
            plugin.sendConfiguredMessage(sender, plugin.configManager().messages().genericError());
            return;
        }

        sender.sendMessage(plugin.parseConfiguredMessage(sender, plugin.configManager().messages().unseparatedPlayers())
            .replaceText(builder -> builder.matchLiteral("<player1>")
                .replacement(SeparationCommandUtils.name(first, firstName)))
            .replaceText(builder -> builder.matchLiteral("<player2>")
                .replacement(SeparationCommandUtils.name(second, secondName))));
    }
}
