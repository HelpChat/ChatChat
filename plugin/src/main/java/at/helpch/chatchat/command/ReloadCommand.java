package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.User;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

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

        var formatsConfig = plugin.configManager().formats();

        int formats = formatsConfig.formats().size();
        sender.sendMessage(Component.text(formats + " " + (formats == 1 ? "format" : "formats") + " loaded!", NamedTextColor.GREEN));
    }
}
