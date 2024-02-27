package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.hooks.towny.AbstractTownyChannel;
import at.helpch.chatchat.util.MessageProcessor;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Join;
import dev.triumphteam.cmd.core.annotation.Optional;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class SwitchChannelCommand extends BaseCommand {

    private final ChatChatPlugin plugin;
    private final String command;

    public SwitchChannelCommand(@NotNull final ChatChatPlugin plugin, @NotNull final String command,
                                @NotNull final List<String> aliases) {
        super(command, aliases);
        this.plugin = plugin;
        this.command = command;
    }

    @Default
    public void switchChannel(final ChatUser user, @Join @Optional @NotNull final String message) {
        final var channels = plugin.configManager().channels().channels();
        final var channel = channels.values()
                .stream()
                .filter(value -> value.commandNames().contains(command))
                .findAny()
                .get(); // this should probably only ever throw if the person has changed command names without
        // restarting

        if (channel instanceof AbstractTownyChannel) {
            final var town = TownyUniverse.getInstance().getResidentOpt(user.uuid())
                    .map(Resident::getTownOrNull);
            if (town.isEmpty() || town.get().isRuined()) { // the API will still see a player in that town if it is ruined
                user.sendMessage(plugin.configManager().messages().userNotInTown());
                return;
            }
        }

        if (!channel.isUsableBy(user)) {
            user.sendMessage(plugin.configManager().messages().channelNoPermission());
            return;
        }

        if (message.isEmpty()) {
            user.channel(channel);
            user.sendMessage(plugin.configManager().messages().channelSwitched()
                    .replaceText(builder -> builder.matchLiteral("<channel>").replacement(channel.name())));
            return;
        }

        MessageProcessor.process(plugin, user, channel, user.channel(), message, false);
    }
}
