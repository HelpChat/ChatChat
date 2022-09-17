package at.helpch.chatchat.api;

import at.helpch.chatchat.api.channel.ChannelTypeRegistry;
import at.helpch.chatchat.api.hook.HookManager;
import at.helpch.chatchat.api.mention.MentionManager;
import at.helpch.chatchat.api.placeholder.MiniPlaceholderManager;
import at.helpch.chatchat.api.rule.RuleManager;
import at.helpch.chatchat.api.user.UsersHolder;
import org.jetbrains.annotations.NotNull;

public interface ChatChatAPI {

    /**
     * Get the {@link UsersHolder}
     *
     * @return The {@link UsersHolder}
     */
    @NotNull UsersHolder usersHolder();

    /**
     * Get the {@link HookManager}
     *
     * @return The {@link HookManager}
     */
    @NotNull HookManager hookManager();

    /**
     * Get the {@link ChannelTypeRegistry}
     *
     * @return The {@link ChannelTypeRegistry}
     */
    @NotNull ChannelTypeRegistry channelTypeRegistry();

    /**
     * Get the {@link RuleManager}
     *
     * @return The {@link RuleManager}
     */
    @NotNull RuleManager ruleManager();

    /**
     * Get the {@link MentionManager}
     *
     * @return The {@link MentionManager}
     */
    @NotNull MentionManager mentionsManager();

    /**
     * Get the {@link MiniPlaceholderManager}
     *
     * @return The {@link MiniPlaceholderManager}
     */
    @NotNull MiniPlaceholderManager miniPlaceholdersManager();
}
