package at.helpch.chatchat.placeholder;

import at.helpch.chatchat.api.placeholder.MiniPlaceholder;
import at.helpch.chatchat.api.placeholder.MiniPlaceholderManager;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import com.google.common.collect.Sets;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

public class MiniPlaceholderManagerImpl implements MiniPlaceholderManager {

    final Set<MiniPlaceholder> miniPlaceholders = Sets.newHashSet();

    @Override
    public void addPlaceholder(@NotNull final MiniPlaceholder placeholder) {
        miniPlaceholders.add(placeholder);
    }

    public @NotNull TagResolver compileTags(
        final boolean inMessage,
        @NotNull final ChatUser sender,
        @NotNull final User recipient
    ) {
        return placeholders().stream()
            .map(placeholder -> placeholder.toTagResolver(inMessage, sender, recipient))
            .filter(tag -> !tag.equals(TagResolver.empty()))
            .collect(TagResolver.toTagResolver());
    }

    @Override
    public @NotNull Set<@NotNull MiniPlaceholder> placeholders() {
        return Collections.unmodifiableSet(miniPlaceholders);
    }

    private void clear() {
        miniPlaceholders.clear();
    }
}
