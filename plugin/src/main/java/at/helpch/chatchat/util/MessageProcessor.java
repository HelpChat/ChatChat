package at.helpch.chatchat.util;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.event.ChatChatEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class MessageProcessor {
    private static final String UTF_PERMISSION = "chatchat.utf";
    private static final String MENTION_PERMISSION = "chatchat.mention";
    private static final String MENTION_EVERYONE_PERMISSION = "chatchat.mention.everyone";
    private static final Sound sound = Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1f, 0.75f);


    public static void process(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final ChatUser user,
        @NotNull final Channel channel,
        @NotNull final String message,
        final boolean async
    ) {
        if (StringUtils.containsIllegalChars(message) && !user.player().hasPermission(UTF_PERMISSION)) {
            user.sendMessage(Component.text("You can't use special characters in chat!", NamedTextColor.RED));
            return;
        }

        final var format = FormatUtils.findFormat(user.player(), plugin.configManager().formats());

        final var chatEvent = new ChatChatEvent(
            async,
            user,
            format,
            Component.text(message),
            channel
        );

        plugin.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        if (user.player().hasPermission(MENTION_PERMISSION)) {
            final var canMentionEveryone = user.player().hasPermission(MENTION_EVERYONE_PERMISSION);
            final var prefix = plugin.configManager().settings().getMentionPrefix();
            for (final String word: message.split(" ")) {
                if (!word.startsWith(prefix)) continue;
                final var name = word.substring(1);

                if (canMentionEveryone && (name.equals("everyone") || name.equals("here"))) {
                    channel.playSound(sound);
                    break;
                }

                final var player = Bukkit.getPlayer(name);
                if (player == null) continue;
                plugin.usersHolder().getUser(player).playSound(sound);
            }

        }

        final var oldChannel = user.channel();
        user.channel(channel);
        chatEvent.channel().sendMessage(chatEvent.user(), FormatUtils.parseFormat(
            chatEvent.format(),
            user.player(),
            chatEvent.message()
        ));
        user.channel(oldChannel);
    }
}
