package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.event.ChatChatEvent;
import at.helpch.chatchat.util.FormatUtils;
import at.helpch.chatchat.util.StringUtils;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Join;
import dev.triumphteam.cmd.core.annotation.Optional;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SwitchChannelCommand extends BaseCommand {

    private static final String UTF_PERMISSION = "chatchat.utf";
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

        if (StringUtils.containsIllegalChars(message) && !user.player().hasPermission(UTF_PERMISSION)) {
            user.sendMessage(Component.text("You can't use special characters in chat!", NamedTextColor.RED));
            return;
        }

        final var format = FormatUtils.findFormat(user.player(), plugin.configManager().formats());

        final var audience = plugin.usersHolder().users()
            .stream()
            .filter(otherUser -> otherUser.canSee(channel)) // get everyone who can see this channel
            .collect(Audience.toAudience());

        final var chatEvent = new ChatChatEvent(
            false,
            user,
            audience,
            format,
            Component.text(message),
            channel
        );

        plugin.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        chatEvent.recipients().sendMessage(FormatUtils.parseFormat(
            chatEvent.format(),
            user.player(),
            chatEvent.message()
        ));
    }
}
