package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Format;
import at.helpch.chatchat.api.User;
import at.helpch.chatchat.util.FormatUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public final class ChatListener implements Listener {

    private final @NotNull ChatChatPlugin plugin;

    public ChatListener(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        var player = event.getPlayer();
        var user = plugin.usersHolder().getUser(player);

        var formatsConfig = plugin.configManager().formats();
        if (formatsConfig == null) { // config is null, so we use the internal default format
            sendDefaultFormat(user, event.getMessage());
            return;
        }

        var formatOptional = FormatUtils.findFormat(player, formatsConfig.formats());
        if (formatOptional.isPresent()) {
            var format = formatOptional.get();
            user.format(format); // set the format of the player

            sendToAudience(parseFormat(format, player, event.getMessage()));
            return;
        }

        // player doesn't have any perms, so find default-format
        var defaultFormat = formatsConfig.formats().get(formatsConfig.defaultFormat());
        if (defaultFormat == null) { // config doesn't have a default format set correctly... user error
            sendDefaultFormat(user, event.getMessage());
            return;
        }

        user.format(defaultFormat);
        sendToAudience(parseFormat(defaultFormat, player, event.getMessage()));
    }

    // parse format with placeholders
    private Component parseFormat(
            @NotNull final Format format,
            @NotNull final Player player,
            @NotNull final String message) {
        return format.getParts().stream()
                .map(part -> PlaceholderAPI.setPlaceholders(player, part))
                .map(part -> part.replace("%message%", message))
                .map(FormatUtils::parseToMiniMessage)
                .collect(Component.toComponent());
    }

    private void sendDefaultFormat(@NotNull final User user, @NotNull final String message) {
        var format = FormatUtils.createDefaultFormat();
        user.format(format);
        sendToAudience(parseFormat(format, user.player(), message));
    }

    private void sendToAudience(@NotNull final Component format) {
        plugin.audiences().players().sendMessage(format);
    }
}
