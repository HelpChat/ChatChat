package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Default;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
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
    public void switchChannel(final Player player) {
        final var user = plugin.usersHolder().getUser(player.getUniqueId());

        final var channels = plugin.configManager().channels().channels();
        final var channel = channels.values()
                .stream()
                .filter(value -> value.commandName().equals(command))
                .findAny()
                .get(); // this should probably only ever throw if the person has changed command names without restarting

        final var audiencePlayer = plugin.audiences().player(player);

        if (!user.canUse(channel)) {
            audiencePlayer.sendMessage(Component.text("You don't have permission to use this channel!", NamedTextColor.RED));
            return;
        }

        user.channel(channel);
        audiencePlayer.sendMessage(Component.text("You have switched to the " + command + " channel!", NamedTextColor.GREEN));
    }
}
