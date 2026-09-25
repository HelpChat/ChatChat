package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.hooks.towny.AbstractTownyChannel;
import at.helpch.chatchat.util.MessageProcessor;
import at.helpch.chatchat.locale.LocaleMessage;
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
    private final String channelName;

    public SwitchChannelCommand(@NotNull final ChatChatPlugin plugin, @NotNull final String channelName,
                                @NotNull final String command,
                                @NotNull final List<String> aliases) {
        super(command, aliases);
        this.plugin = plugin;
        this.channelName = channelName;
    }

    @Default
    public void switchChannel(final ChatUser user, @Join @Optional @NotNull final String message) {
        final var channel = plugin.configManager().channels().channels().get(channelName);
        if (channel == null) {
            plugin.sendConfiguredMessage(user, LocaleMessage.COMMAND_UNKNOWN_COMMAND);
            return;
        }

        if (channel instanceof AbstractTownyChannel) {
            final var town = TownyUniverse.getInstance().getResidentOpt(user.uuid())
                    .map(Resident::getTownOrNull);
            if (town.isEmpty() || town.get().isRuined()) { // the API will still see a player in that town if it is ruined
                plugin.sendConfiguredMessage(user, LocaleMessage.USER_NOT_IN_TOWN);
                return;
            }
        }

        if (!channel.isUsableBy(user)) {
            plugin.sendConfiguredMessage(user, LocaleMessage.CHANNEL_NO_PERMISSION);
            return;
        }

        if (message.isEmpty()) {
            user.channel(channel);
            user.sendMessage(plugin.parseConfiguredMessage(user, LocaleMessage.CHANNEL_SWITCHED)
                    .replaceText(builder -> builder.matchLiteral("<channel>").replacement(channel.name())));
            return;
        }

        MessageProcessor.process(plugin, user, channel, message, false);
    }
}
