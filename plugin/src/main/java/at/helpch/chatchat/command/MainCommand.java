package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public final class MainCommand extends ChatChatCommand {

    private final ChatChatPlugin plugin;

    public MainCommand(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void defaultCommand(final CommandSender sender) {
        var text = text("A Chat Plugin ", NamedTextColor.AQUA)
                .append(text("by ", NamedTextColor.GRAY)
                .append(text("Help", TextColor.fromCSSHexString("#3dbbe4"))
                .append(text("Chat", TextColor.fromCSSHexString("#f3af4b")))))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://helpch.at"))
                .append(Component.newline())
                .append(text("Version: ", NamedTextColor.GRAY))
                .append(text(plugin.getDescription().getVersion(), NamedTextColor.AQUA));

        plugin.audiences().sender(sender).sendMessage(text);
    }
}
