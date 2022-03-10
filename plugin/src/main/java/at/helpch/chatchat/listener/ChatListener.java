package at.helpch.chatchat.listener;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Format;
import at.helpch.chatchat.api.User;
import at.helpch.chatchat.event.ChatChatEvent;
import at.helpch.chatchat.format.ChatFormat;
import at.helpch.chatchat.util.FormatUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public final class ChatListener implements Listener {

    private final @NotNull ChatChatPlugin plugin;

    public ChatListener(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        var player = event.getPlayer();
        var user = plugin.usersHolder().getUser(player);

        var formatsConfig = plugin.configManager().formats();

        ChatFormat format;
        if (formatsConfig == null) { // config is null, so we use the internal format
            format = FormatUtils.createDefaultFormat();
        } else {
            var formatOptional = FormatUtils.findFormat(player, formatsConfig.formats());
            var defaultFormat = formatsConfig.formats().get(formatsConfig.defaultFormat());

            // if player doesn't have any perms, find default-format and if no default-format is found use the internal format
            format = formatOptional.orElseGet(() -> defaultFormat != null ? defaultFormat : FormatUtils.createDefaultFormat());
        }

        // this will probably be changed when channels will be added
        var audience = plugin.audiences().players();
        var chatEvent = new ChatChatEvent(
            event.isAsynchronous(),
            event.getPlayer(),
            audience,
            format,
            event.getMessage()
        );

        plugin.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        user.format(chatEvent.format());
        chatEvent.recipients().sendMessage(
            parseFormat(chatEvent.format(), chatEvent.player(), chatEvent.message())
        );
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
}