package at.helpch.chatchat.config.holder;

import at.helpch.chatchat.api.format.BasicFormat;
import at.helpch.chatchat.config.DefaultConfigObjects;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

// configurate requires non-final fields
@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public final class MentionSettingsHolder {

    private String prefix = "@";
    private Sound sound = DefaultConfigObjects.createMentionSound();
    private boolean privateMessage = true;
    private BasicFormat personalFormat = DefaultConfigObjects.createPersonalMentionFormat();
    private BasicFormat channelFormat = DefaultConfigObjects.createChannelMentionFormat();

    public @NotNull String prefix() {
        return prefix;
    }

    public @NotNull Sound sound() {
        return sound;
    }

    public boolean privateMessage() {
        return privateMessage;
    }

    public @NotNull BasicFormat personalFormat() {
        return personalFormat;
    }

    public @NotNull BasicFormat channelFormat() {
        return channelFormat;
    }
}
