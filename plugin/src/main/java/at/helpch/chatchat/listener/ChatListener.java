package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.User;
import at.helpch.chatchat.api.event.ChatChatEvent;
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
        final var oldChannel = user.channel(); // save their old channel in case they use a message prefix

        final var channelByPrefix =
                ChannelUtils.findChannelByPrefix(List.copyOf(plugin.configManager().channels().channels().values()), event.getMessage());
        channelByPrefix.ifPresent(channel -> {
            if (user.canUse(channel)) user.channel(channel);  // set the channel if their message starts with a channel prefix & has perms
        });

        final var message = channelByPrefix.isEmpty()
                ? event.getMessage()
                : event.getMessage().replaceFirst(Pattern.quote(channelByPrefix.get().messagePrefix()), "");

        final var newChannel = user.channel();

        final var audience = plugin.usersHolder().users()
                .stream()
                .filter(otherUser -> otherUser.canSee(newChannel)) // get everyone who can see this channel
                .map(User::player)
                .map(plugin.audiences()::player)
                .collect(Audience.toAudience());

        final var chatEvent = new ChatChatEvent(
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
        channelByPrefix.ifPresent(unused -> user.channel(oldChannel)); // set them back to their old channel if they used a prefix
        chatEvent.recipients().sendMessage(
            FormatUtils.parseFormat(chatEvent.format(), user, chatEvent.message()));
    }
}
