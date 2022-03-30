package at.helpch.chatchat.util;

import net.kyori.adventure.text.Component;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class MentionUtils {
    public static final class MentionReplaceResult {
        private final boolean didReplace;
        private final Component component;

        public MentionReplaceResult(final boolean didReplace, final Component component) {
            this.didReplace = didReplace;
            this.component = component;
        }

        public boolean didReplace() {
            return didReplace;
        }

        public Component component() {
            return component;
        }
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static MentionReplaceResult replaceMention(
            @RegExp @NotNull final String username,
            @NotNull final Component component,
            @NotNull final String format) {
        final AtomicBoolean hasBeenReplaced = new AtomicBoolean();
        final var replaced = component.replaceText(builder -> builder
                .match(Pattern.compile(username, Pattern.CASE_INSENSITIVE))
                .replacement((result, ignored) -> {
                            hasBeenReplaced.set(true);
                            return MessageUtils.parseToMiniMessage(format + result.group());
                        }
                ));
        return new MentionReplaceResult(hasBeenReplaced.get(), replaced);
    }
}
