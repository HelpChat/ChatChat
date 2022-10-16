package at.helpch.chatchat.config.holder;

import at.helpch.chatchat.api.format.SimpleFormat;
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
    private SimpleFormat personalFormat = DefaultConfigObjects.createPersonalMentionFormat();
    private SimpleFormat channelFormat = DefaultConfigObjects.createChannelMentionFormat();

    public @NotNull String prefix() {
        return prefix;
    }

    public @NotNull Sound sound() {
        return sound;
    }

    public boolean privateMessage() {
        return privateMessage;
    }

    public @NotNull SimpleFormat personalFormat() {
        return personalFormat;
    }

    public @NotNull SimpleFormat channelFormat() {
        return channelFormat;
    }
}
