package at.helpch.chatchat.mention;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.mention.Mention;
import at.helpch.chatchat.api.mention.MentionManager;
import at.helpch.chatchat.api.user.User;
import com.google.common.collect.Sets;

import java.util.Collections;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import org.jetbrains.annotations.Nullable;

public class MentionManagerImpl implements MentionManager {

    private final Set<Mention> mentions = Sets.newHashSet();

    public MentionManagerImpl(@NotNull final ChatChatPlugin plugin) {
        mentions.add(new PersonalMention(plugin));
        mentions.add(new ChannelMention(plugin));
    }

    @Override
    public void addMention(@NotNull Mention mention) {
        mentions.add(mention);
    }

    @Override
    public @NotNull Set<Mention> mentions() {
        return Collections.unmodifiableSet(mentions);
    }

    public @NotNull MentionResultImpl processMentions(
        final boolean async,
        @NotNull final User sender,
        @NotNull final User target,
        @NotNull final Channel channel,
        @NotNull final Component message,
        @Nullable final Object data
    ) {
        var processedMessage = message;
        var mentioned = false;
        var playSound = false;

        for (final Mention mention : mentions) {
            final var result = mention.processMention(async, sender, target, channel, processedMessage, data);
            if (result.mentioned() && result.playSound()) {
                mentioned = true;
                playSound = true;
            } else if (result.mentioned()) {
                mentioned = true;
            }

            processedMessage = result.message();
        }

        return new MentionResultImpl(processedMessage, mentioned, playSound);
    }
}
