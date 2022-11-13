package at.helpch.chatchat.config.holder;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

// configurate requires non-final fields
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public final class SettingsHolder {

    private PMSettingsHolder privateMessages = new PMSettingsHolder();

    private String itemFormat = "<gray>[</gray><item><gray> x <amount>]";
    private String itemFormatInfo = "<dark_gray><item> x <amount>";
    private MentionSettingsHolder mentions = new MentionSettingsHolder();
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

    public @NotNull MentionSettingsHolder mentions() {
        return mentions;
    }

    public long lastMessagedCacheDuration() {
        return lastMessagedCacheDuration;
    }
}
