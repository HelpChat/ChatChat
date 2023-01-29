package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.User;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public final class ReloadCommand extends ChatChatCommand {

    private static final String ADMIN_PERMISSION = "chatchat.admin";
    private final ChatChatPlugin plugin;

    public ReloadCommand(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @SubCommand("reload")
    @Permission(ADMIN_PERMISSION)
    public void reloadCommand(final User sender) {
        plugin.configManager().reload();

        final int formats = plugin.configManager().formats().formats().size();
        final int channels = plugin.configManager().channels().channels().size();
        final int channelFormats = plugin.configManager().channels().channels().values().stream()
            .mapToInt(channel -> channel.formats().formats().size())
            .sum();

        sender.sendMessage(text("Chat", TextColor.fromCSSHexString("#40c9ff"))
                .append(text("Chat", TextColor.fromCSSHexString("#e81cff")))
                .append(text(" Reloaded Successfully!", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(text(formats, NamedTextColor.WHITE))
                .append(text((formats == 1 ? " format" : " formats") + " loaded!", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(text(channels, NamedTextColor.WHITE))
                .append(text((channels == 1 ? " channel" : " channels") + " loaded!", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(text(channelFormats, NamedTextColor.WHITE))
                .append(text((channelFormats == 1 ? " channel format" : " channel formats") + " loaded!", NamedTextColor.GREEN))
        );
    }
}
