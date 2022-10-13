package at.helpch.chatchat.mention;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.event.PersonalMentionEvent;
import at.helpch.chatchat.api.mention.Mention;
import at.helpch.chatchat.api.mention.MentionResult;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import at.helpch.chatchat.util.MentionUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PersonalMention implements Mention {

    private final ChatChatPlugin plugin;

    public PersonalMention(@NotNull final ChatChatPlugin plugin) {
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

        final var chatSender = (ChatUser) sender;

        if (!(target instanceof ChatUser)) {
            return unprocessedResult;
        }

        final var chatTarget = (ChatUser) target;

        final var result = MentionUtils.processPersonalMentions(
            plugin.configManager().settings().mentions().prefix(),
            plugin.configManager().settings().mentions().personalFormat(),
            chatSender,
            chatTarget,
            message
        );

        if (!result.getKey()) {
            return unprocessedResult;
        }

        // In this case the data tells us if the event should be fired or not.
        if (data instanceof Boolean && !((boolean) data)) {
            return new MentionResultImpl(result.getValue(), true, chatSender.canSee(chatTarget));
        }

        final var event = new PersonalMentionEvent(async, chatSender, chatTarget, channel, chatSender.canSee(chatTarget));
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return unprocessedResult;
        }

        return new MentionResultImpl(result.getValue(), true, event.playSound());
    }
}
