package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.format.PriorityFormat;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.placeholder.MiniPlaceholderContext;
import at.helpch.chatchat.user.ConsoleUser;
import at.helpch.chatchat.util.FormatUtils;
import at.helpch.chatchat.util.MessageProcessor;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.Join;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.jetbrains.annotations.NotNull;

public class FormatTestCommand extends ChatChatCommand {

    private static final String FORMAT_TEST_PERMISSION = "chatchat.test.format";

    private final ChatChatPlugin plugin;

    public FormatTestCommand(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @SubCommand("test")
    @Permission(FORMAT_TEST_PERMISSION)
    public void testFormat(
        @NotNull final ChatUser sender,
        @NotNull final PriorityFormat format,
        @Join @NotNull final String message
    ) {
        var player = sender.player();
        if (player.isEmpty()) {
            sender.sendMessage(plugin.configManager().messages().genericError());
            return;
        }

        if (message.isBlank()) {
            sender.sendMessage(plugin.configManager().messages().emptyMessage());
            return;
        }

        sender.sendMessage(
            FormatUtils.parseFormat(
                format,
                player.get(),
                player.get(),
                MessageProcessor.processMessage(plugin, sender, ConsoleUser.INSTANCE, message),
                plugin.miniPlaceholdersManager().compileTags(MiniPlaceholderContext.builder().inMessage(false).sender(sender).recipient(sender).build())
            )
        );
    }

}
