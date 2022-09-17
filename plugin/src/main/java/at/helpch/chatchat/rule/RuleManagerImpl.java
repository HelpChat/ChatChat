package at.helpch.chatchat.rule;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.rule.Rule;
import at.helpch.chatchat.api.rule.RuleManager;
import at.helpch.chatchat.api.user.ChatUser;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class RuleManagerImpl implements RuleManager {

    private final ChatChatPlugin plugin;

    private final Set<Rule> publicChatRules = Sets.newHashSet();
    private final Set<Rule> privateChatRules = Sets.newHashSet();

    public RuleManagerImpl(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
        publicChatRules.add(new InvalidCharsRule(plugin));
        privateChatRules.add(new InvalidCharsRule(plugin));
    }

    @Override
    public void addPublicChatRule(@NotNull final Rule rule) {
        this.publicChatRules.add(rule);
    }

    @Override
    public void addPrivateChatRule(@NotNull final Rule rule) {
        this.privateChatRules.add(rule);
    }

    @Override
    public @NotNull Set<Rule> publicChatRules() {
        return Collections.unmodifiableSet(publicChatRules);
    }

    @Override
    public @NotNull Set<Rule> privateChatRules() {
        return Collections.unmodifiableSet(privateChatRules);
    }

    public Optional<Component> isAllowedPublicChat(@NotNull final ChatUser sender, @NotNull final String message) {
        final var unfulfilledRules = publicChatRules()
            .stream()
            .filter(rule -> !rule.isAllowedPublic(sender, message))
            .collect(Collectors.toList());

        if (unfulfilledRules.isEmpty()) {
            return Optional.empty();
        }

        return unfulfilledRules.stream()
            .map(Rule::publicDeniedMessage)
            .filter(Optional::isPresent)
            .findFirst()
            .orElse(Optional.of(plugin.configManager().messages().invalidMessage()));
    }

    public Optional<Component> isAllowedPrivateChat(
        @NotNull final ChatUser sender,
        @NotNull final ChatUser recipient,
        @NotNull final String message
    ) {
        final var unfulfilledRules = publicChatRules()
            .stream()
            .filter(rule -> !rule.isAllowedPrivate(sender, recipient, message))
            .collect(Collectors.toList());

        if (unfulfilledRules.isEmpty()) {
            return Optional.empty();
        }

        return unfulfilledRules.stream()
            .map(Rule::privateDeniedMessage)
            .filter(Optional::isPresent)
            .findFirst()
            .orElse(Optional.of(plugin.configManager().messages().invalidMessage()));
    }
}
