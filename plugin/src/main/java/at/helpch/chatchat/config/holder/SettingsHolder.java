package at.helpch.chatchat.config.holder;

import at.helpch.chatchat.config.DefaultConfigObjects;
import at.helpch.chatchat.format.BasicFormat;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

// configurate requires non-final fields
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public final class SettingsHolder {

    private PMSettingsHolder privateMessages = new PMSettingsHolder();

    private String itemFormat = "<gray>[</gray><item><gray> x <amount>]";
    private String itemFormatInfo = "<dark_gray><item> x <amount>";

    private String mentionPrefix = "@";
    private BasicFormat mentionFormat = DefaultConfigObjects.createMentionFormat();
    private String channelMentionFormat = "<yellow>";
    private Sound mentionSound = DefaultConfigObjects.createMentionSound();
    private boolean mentionOnMessage = true;
    private long lastMessagedCacheDuration = 300;

    public @NotNull PMSettingsHolder privateMessagesSettings() {
        return privateMessages;
    }

    public @NotNull String itemFormat() {
        return itemFormat;
    }

    public @NotNull String itemFormatInfo() {
        return itemFormatInfo;
    }

    public @NotNull String mentionPrefix() {
        return mentionPrefix;
    }

    public @NotNull BasicFormat mentionFormat() {
        return mentionFormat;
    }

    public @NotNull String channelMentionFormat() {
        return channelMentionFormat;
    }

    public @NotNull Sound mentionSound() {
        return mentionSound;
    }

    public boolean mentionOnMessage() {
        return mentionOnMessage;
    }

    public long lastMessagedCacheDuration() {
        return lastMessagedCacheDuration;
    }
}
