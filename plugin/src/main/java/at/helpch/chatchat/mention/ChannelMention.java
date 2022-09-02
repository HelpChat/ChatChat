package at.helpch.chatchat.mention;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.event.ChannelMentionEvent;
import at.helpch.chatchat.api.mention.Mention;
import at.helpch.chatchat.api.mention.MentionResult;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import at.helpch.chatchat.util.MentionUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelMention implements Mention {

    private final ChatChatPlugin plugin;

    public ChannelMention(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull MentionResult processMention(
        final boolean async,
        @NotNull final User sender,
        @NotNull final User target,
        @NotNull final Channel channel,
        @NotNull final Component message,
        @Nullable final Object data
    ) {
        final var unprocessedResult = new MentionResultImpl(message, false, false);

        if (plugin.configManager().settings().mentions().prefix().isBlank()) {
            return unprocessedResult;
        }

        if (!(sender instanceof ChatUser)) {
            return unprocessedResult;
        }

        // In this case the data tells us that we don't want this mention to be parsed at all. We want this when the
        // sender's message is updated to include the target's mentions. We don't want to include channel mentions there
        // as those will be parsed on their own.
        if (data instanceof Boolean && !((boolean) data)) {
            return unprocessedResult;
        }

        final var chatSender = (ChatUser) sender;

        final var result = MentionUtils.processChannelMentions(
            plugin.configManager().settings().mentions().prefix(),
            plugin.configManager().settings().mentions().personalFormat(),
            chatSender,
            target,
            message
        );

        if (!result.getKey()) {
            return unprocessedResult;
        }

        final var event = new ChannelMentionEvent(async, chatSender, target, channel, chatSender.canSee(target));
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return unprocessedResult;
        }

        return new MentionResultImpl(result.getValue(), true, event.playSound());
    }
}
