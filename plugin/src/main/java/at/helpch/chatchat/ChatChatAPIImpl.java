package at.helpch.chatchat;

import at.helpch.chatchat.api.ChatChatAPI;
import at.helpch.chatchat.channel.ChannelTypeRegistryImpl;
import at.helpch.chatchat.hooks.HookManagerImpl;
import at.helpch.chatchat.rule.RuleManagerImpl;
import at.helpch.chatchat.user.UsersHolderImpl;
import org.jetbrains.annotations.NotNull;

public class ChatChatAPIImpl implements ChatChatAPI {

    private final ChatChatPlugin plugin;

    public ChatChatAPIImpl(ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    public @NotNull UsersHolderImpl usersHolder() {
        return plugin.usersHolder();
    }

    public @NotNull HookManagerImpl hookManager() {
        return plugin.hookManager();
    }

    public @NotNull ChannelTypeRegistryImpl channelTypeRegistry() {
        return plugin.channelTypeRegistry();
    }

    public @NotNull RuleManagerImpl ruleManager() {
        return plugin.ruleManager();
    }

    public @NotNull ChatChatPlugin plugin() {
        return plugin;
    }
}
