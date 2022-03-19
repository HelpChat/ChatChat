package at.helpch.chatchat.user;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.Format;
import at.helpch.chatchat.api.User;
import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.format.ChatFormat;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class ConsoleUser implements User {

    @Override
    public @NotNull Channel channel() {
        return ChatChannel.defaultChannel();
    }

    @Override
    public void channel(@NotNull final Channel channel) {}

    @Override
    public @NotNull Format format() {
        return ChatFormat.defaultFormat();
    }

    @Override
    public void format(@NotNull final Format format) {}

    @Override
    public @NotNull UUID uuid() {
        return Identity.nil().uuid();
    }

    @Override
    public boolean canSee(@NotNull final Channel channel) {
        return true;
    }

    @Override
    public boolean canUse(@NotNull final Channel channel) {
        return true;
    }

    @Override
    public @NotNull Audience audience() {
        return ChatChatPlugin.audiences().console();
    }

    @Override
    public @NotNull Identity identity() {
        return Identity.nil();
    }
}
