package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.processor.LocalToLocalMessageProcessor;
import at.helpch.chatchat.user.ConsoleUser;
import at.helpch.chatchat.util.ChannelUtils;
import at.helpch.chatchat.util.FormatUtils;
import at.helpch.chatchat.processor.MessageProcessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.UnknownFormatConversionException;
import java.util.regex.Pattern;

public final class ChatListener implements Listener {

    private static final Pattern LEGACY_FORMATS_PATTERN = Pattern.compile("§[\\da-fk-or]");
    private static final Pattern LEGACY_HEX_COLOR_PATTERN = Pattern.compile("§x(§[\\da-fA-F]){6}");
    private final ChatChatPlugin plugin;

    public ChatListener(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, String> pendingToConfirm = new HashMap<>();

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

        Channel currentChannel = user.channel();

        if (currentChannel.name().equalsIgnoreCase("shout")) {
            user.channel(ChatChannel.defaultChannel());
        }

        if (!user.chatEnabled()) {
            event.setCancelled(true);
            user.sendMessage(plugin.configManager().messages().chatDisabled());
            return;
        }

        final var channelByPrefix =
            ChannelUtils.findChannelByPrefix(
                List.copyOf(plugin.configManager().channels().channels().values()),
                event.getMessage());

        final var message = channelByPrefix.isEmpty() || !channelByPrefix.get().isUsableBy(user)
            ? event.getMessage()
            : event.getMessage().replaceFirst(Pattern.quote(channelByPrefix.get().messagePrefix()), "");

        if (message.isEmpty() || message.trim().isEmpty()) {
            event.setCancelled(true);
            user.sendMessage(plugin.configManager().messages().messageEmpty());
            return;
        }

        var channel = channelByPrefix.isEmpty() || !channelByPrefix.get().isUsableBy(user)
            ? user.channel()
            : channelByPrefix.get();

        // Ensure the user still has the channel permission, if not reset them back to the default channel
        if (!channel.isUsableBy(user)) {
            event.setCancelled(true);

            user.channel(ChatChannel.defaultChannel());
            user.sendMessage(plugin.configManager().messages().channelNoPermissionSwitch()
                .replaceText(builder -> builder.matchLiteral("<default>").replacement(ChatChannel.defaultChannel().name())));
            return;
        }

        final var consoleFormat = plugin.configManager().formats().consoleFormat();

        final var oldChannel = user.channel();

        // We switch the user to the channel here so that the console can parse the correct channel prefix
        user.channel(channel);

        Component processedMessage = LocalToLocalMessageProcessor.processMessage(plugin, user, ConsoleUser.INSTANCE, message);

        if (channel.name().equalsIgnoreCase("shout")) {
            if (pendingToConfirm.containsKey(player.getUniqueId())) {
                String current = pendingToConfirm.get(player.getUniqueId());

                if (current.equalsIgnoreCase(message)) {
                    pendingToConfirm.remove(player.getUniqueId());
                } else {
                    pendingToConfirm.put(player.getUniqueId(), message);
                    user.sendMessage(plugin.configManager().messages().repeatToConfirm()
                        .replaceText(builder -> builder.matchLiteral("<message>").replacement(processedMessage)));
                    event.setCancelled(true);
                    return;
                }
            } else {
                String stored = pendingToConfirm.get(player.getUniqueId());

                Component repeatResponse = plugin.configManager().messages().repeatToConfirm();
                if(stored == null) {
                    pendingToConfirm.put(player.getUniqueId(), message);

                    repeatResponse = repeatResponse.replaceText(builder -> builder.matchLiteral("<message>").replacement(processedMessage));
                    user.sendMessage(repeatResponse);
                    event.setCancelled(true);
                    return;
                }

                if(stored.equalsIgnoreCase(message)) {
                    pendingToConfirm.remove(player.getUniqueId());
                } else {
                    pendingToConfirm.put(player.getUniqueId(), message);
                    repeatResponse = repeatResponse.replaceText(builder -> builder.matchLiteral("<message>").replacement(processedMessage));
                    user.sendMessage(repeatResponse);
                    event.setCancelled(true);
                    return;
                }
            }
        }

        event.setMessage(LegacyComponentSerializer.legacySection().serialize(processedMessage));

        try {
            event.setFormat(LegacyComponentSerializer.legacySection().serialize(
                FormatUtils.parseConsoleFormat(consoleFormat, player)
            ));
        } catch (UnknownFormatConversionException exception) {
            plugin.getLogger().severe(
                "Your console format contains illegal characters: '%" +
                    exception
                        .getMessage()
                        .replace("Conversion = ", "")
                        .replace("'", "") +
                    "'. You cannot use the % symbol in your console format.");
            plugin.getLogger().severe(
                "Make sure that all the PlaceholderAPI expansions for the placeholders you use in your console " +
                    "format are installed and work properly.");
        }

        // Cancel the event if the message doesn't end up being sent
        // This only happens if the message contains illegal characters or if the ChatChatEvent is canceled.
        try {
            event.setCancelled(!MessageProcessor.processMessageEvent(plugin, user, channel, message, event.isAsynchronous()));
        } catch (Exception e) {
            plugin.getLogger().severe("An error occurred while processing a message from " + player.getName() + ":");
            plugin.getLogger().severe("Message: " + event.getMessage());
            e.printStackTrace();
            user.channel(oldChannel);
        }
        user.channel(oldChannel);
    }

    private static String cleanseMessage(@NotNull final String message) {
        return LEGACY_FORMATS_PATTERN.matcher(
            LEGACY_HEX_COLOR_PATTERN.matcher(message).replaceAll("")
        ).replaceAll("").replace("§", "");
    }

}
