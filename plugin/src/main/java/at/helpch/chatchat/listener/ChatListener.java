package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.util.ChannelUtils;
import at.helpch.chatchat.util.MessageProcessor;
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
        final var user = (ChatUser) plugin.usersHolder().getUser(player);

        final var channelByPrefix =
                ChannelUtils.findChannelByPrefix(List.copyOf(plugin.configManager().channels().channels().values()), event.getMessage());

        final var message = channelByPrefix.isEmpty()
                ? event.getMessage()
                : event.getMessage().replaceFirst(Pattern.quote(channelByPrefix.get().messagePrefix()), "");

        final var channel = channelByPrefix.isEmpty() || !channelByPrefix.get().isUseableBy(user) ? user.channel() : channelByPrefix.get();

        MessageProcessor.process(plugin, user, channel, message, event.isAsynchronous());
    }
}
