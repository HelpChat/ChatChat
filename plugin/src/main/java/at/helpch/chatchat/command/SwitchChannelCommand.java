package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.processor.MessageProcessor;
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
        final var channelOpt = plugin.configManager().channels().channels()
            .values()
            .stream()
            .filter(value -> value.commandNames().contains(command))
            .findAny();

        if (channelOpt.isPresent()) {
            final var channel = channelOpt.get();

            if (!channel.isUsableBy(user)) {
                user.sendMessage(plugin.configManager().messages().channelNoPermission());
                return;
            }

            var currentChannel = user.channel();

            user.channel(channel);

            if (!message.isEmpty() && user.hasPermission("chatchat.channel-switch-send")) {
                MessageProcessor.processMessageEvent(plugin, user, channel, message, false);

                if(!currentChannel.equals(channel)) {
                    user.channel(currentChannel);
                }
            } else {
                user.sendMessage(plugin.configManager().messages().channelSwitched()
                    .replaceText(builder -> builder.matchLiteral("<channel>").replacement(channel.name())));
            }
        } else {
            Channel def = plugin.configManager().channels().channels().get("default");

            if (def != null && def.isUsableBy(user)) {
                user.channel(def);
                user.sendMessage(plugin.configManager().messages().channelSwitched()
                    .replaceText(builder -> builder.matchLiteral("<channel>").replacement(def.name())));
            }
        }
    }

}
