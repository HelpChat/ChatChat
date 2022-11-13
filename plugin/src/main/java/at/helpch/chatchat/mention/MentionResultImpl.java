package at.helpch.chatchat.mention;

import at.helpch.chatchat.api.mention.MentionResult;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class MentionResultImpl implements MentionResult {

    private final Component message;
    private final boolean mentioned;
    private final boolean playSound;

    public MentionResultImpl(@NotNull final Component message, final boolean mentioned, final boolean playSound) {
        this.message = message;
        this.mentioned = mentioned;
        this.playSound = playSound;
    }

    @Override
    public @NotNull Component message() {
        return message;
    }

    @Override
    public boolean mentioned() {
        return mentioned;
    }

    @Override
    public boolean playSound() {
        return playSound;
    }

}
