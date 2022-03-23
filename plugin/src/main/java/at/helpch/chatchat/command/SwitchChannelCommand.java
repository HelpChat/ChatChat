package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.util.MessageProcessor;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Join;
import dev.triumphteam.cmd.core.annotation.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public final class SwitchChannelCommand extends BaseCommand {

    private final ChatChatPlugin plugin;
    private final String command;

    public SwitchChannelCommand(@NotNull final ChatChatPlugin plugin, @NotNull final String command) {
        super(command);
        this.plugin = plugin;
        this.command = command;
    }

    @Default
    public void switchChannel(final ChatUser user, @Join @Optional @NotNull final String message) {
        final var channels = plugin.configManager().channels().channels();
        final var channel = channels.values()
            .stream()
            .filter(value -> value.commandName().equals(command))
            .findAny()
            .get(); // this should probably only ever throw if the person has changed command names without restarting

        if (!user.canUse(channel)) {
            user.sendMessage(Component.text("You don't have permission to use this channel!", NamedTextColor.RED));
            return;
        }

        if (message.isEmpty()) {
            user.channel(channel);
            user.sendMessage(Component.text("You have switched to the " + command + " channel!", NamedTextColor.GREEN));
            return;
        }

        MessageProcessor.process(plugin, user, channel, message, false);
    }
}
