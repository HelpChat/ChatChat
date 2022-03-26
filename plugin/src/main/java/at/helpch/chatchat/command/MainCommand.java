package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.User;
import at.helpch.chatchat.util.MessageUtils;
import dev.triumphteam.cmd.core.annotation.Default;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainCommand extends ChatChatCommand {

    private static final JavaPlugin PLUGIN = JavaPlugin.getProvidingPlugin(ChatChatPlugin.class);
    private static final Component TEXT = MessageUtils.parseToMiniMessage(
            "<aqua><click:open_url:'https://helpch.at'>A Chat Plugin <gray>by <#3dbbe4>Help<#f3af4b>Chat<br><gray>Version: <aqua>" + PLUGIN.getDescription().getVersion());

    @Default
    public void defaultCommand(final User sender) {
        sender.sendMessage(TEXT);
    }
}
