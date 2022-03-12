package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.User;
import at.helpch.chatchat.api.event.ChatChatEvent;
import at.helpch.chatchat.format.ChatFormat;
import at.helpch.chatchat.util.ChannelUtils;
import at.helpch.chatchat.util.FormatUtils;
import net.kyori.adventure.audience.Audience;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

public final class ChatListener implements Listener {

    private final ChatChatPlugin plugin;

    public ChatListener(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        final var player = event.getPlayer();
        final var user = plugin.usersHolder().getUser(player);

        final var format = FormatUtils.findFormat(player, plugin.configManager().formats());

        var message = event.getMessage();
        final var channelByPrefix =
                ChannelUtils.findChannelByPrefix(List.copyOf(plugin.configManager().channels().channels().values()), event.getMessage());
        if (channelByPrefix.isPresent()) {
            var channel = channelByPrefix.get();
            user.channel(channel); // set the channel if their message starts with a channel prefix
            message = event.getMessage().replaceFirst(
                    Pattern.quote(channel.messagePrefix()), ""); // remove the message prefix
        }

        final var channel = user.channel();

        final var audience = plugin.usersHolder().users()
                .stream()
                .filter(otherUser -> otherUser.channel().equals(channel)) // get everyone in the same channel
                .map(User::player)
                .map(plugin.audiences()::player)
                .collect(Audience.toAudience());

        var chatEvent = new ChatChatEvent(
            event.isAsynchronous(),
            event.getPlayer(),
            audience,
            format,
            message
        );

        plugin.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        user.format(chatEvent.format());
        chatEvent.recipients().sendMessage(
            FormatUtils.parseFormat(chatEvent.format(), user, chatEvent.message()));
    }
}
