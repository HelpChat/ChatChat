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

    private static final Pattern LEGACY_COLOR_PATTERN = Pattern.compile("§[\\da-f]");
    private static final Pattern LEGACY_HEX_COLOR_PATTERN = Pattern.compile("§x(§[\\da-fA-F]){6}");
    private final ChatChatPlugin plugin;

    public ChatListener(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        try {
            event.getRecipients().clear();
        } catch (UnsupportedOperationException ignored) {
            // a plugin is doing something weird so all we can do is cancel
            event.setCancelled(true);
        }

        event.setMessage(cleanseMessage(event.getMessage()));

        final var player = event.getPlayer();
        final var user = (ChatUser) plugin.usersHolder().getUser(player);

        final var channelByPrefix =
                ChannelUtils.findChannelByPrefix(List.copyOf(plugin.configManager().channels().channels().values()), event.getMessage());

        final var message = channelByPrefix.isEmpty() || !channelByPrefix.get().isUseableBy(user)
                ? event.getMessage()
                : event.getMessage().replaceFirst(Pattern.quote(channelByPrefix.get().messagePrefix()), "");

        final var channel = channelByPrefix.isEmpty() || !channelByPrefix.get().isUseableBy(user) ? user.channel() : channelByPrefix.get();

        MessageProcessor.process(plugin, user, channel, message, event.isAsynchronous());
    }

    private static String cleanseMessage(@NotNull final String message) {
        // TODO: Maybe kyorify instead
        return LEGACY_COLOR_PATTERN.matcher(
            LEGACY_HEX_COLOR_PATTERN.matcher(message).replaceAll("")
        ).replaceAll("");
    }
}
