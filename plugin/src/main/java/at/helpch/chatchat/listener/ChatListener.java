package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.event.ChatChatEvent;
import at.helpch.chatchat.util.ChannelUtils;
import at.helpch.chatchat.util.FormatUtils;
import at.helpch.chatchat.util.StringUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

public final class ChatListener implements Listener {

    private static final String UTF_PERMISSION = "chatchat.utf";
    private final ChatChatPlugin plugin;

    public ChatListener(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        final var player = event.getPlayer();
        final var user = (ChatUser) plugin.usersHolder().getUser(player);

        final var format = FormatUtils.findFormat(player, plugin.configManager().formats());

        final var channelByPrefix =
                ChannelUtils.findChannelByPrefix(List.copyOf(plugin.configManager().channels().channels().values()), event.getMessage());

        final var message = channelByPrefix.isEmpty()
                ? event.getMessage()
                : event.getMessage().replaceFirst(Pattern.quote(channelByPrefix.get().messagePrefix()), "");

        if (StringUtils.containsIllegalChars(message) && !user.player().hasPermission(UTF_PERMISSION)) {
            user.sendMessage(Component.text("You can't use special characters in chat!", NamedTextColor.RED));
            return;
        }

        final var channel = channelByPrefix.isEmpty() || !user.canUse(channelByPrefix.get()) ? user.channel() : channelByPrefix.get();

        final var audience = plugin.usersHolder().users()
                .stream()
                .filter(otherUser -> otherUser.canSee(channel)) // get everyone who can see this channel
                .collect(Audience.toAudience());

        final var chatEvent = new ChatChatEvent(
                event.isAsynchronous(),
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

        final var oldChannel = user.channel();
        user.channel(channel);
        chatEvent.recipients().sendMessage(FormatUtils.parseFormat(
            chatEvent.format(),
            player,
            chatEvent.message()
        ));
        user.channel(oldChannel);
    }
}
