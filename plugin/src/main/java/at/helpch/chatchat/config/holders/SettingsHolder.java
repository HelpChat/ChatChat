package at.helpch.chatchat.config.holders;

import at.helpch.chatchat.config.DefaultConfigObjects;
import at.helpch.chatchat.format.PMFormat;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

// configurate requires non-final fields
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@ConfigSerializable
public final class SettingsHolder {

    private PMFormat senderFormat = DefaultConfigObjects.createPrivateMessageSenderFormat();

    private PMFormat recipientFormat = DefaultConfigObjects.createPrivateMessageRecipientFormat();
    private PMFormat socialSpyFormat = DefaultConfigObjects.createPrivateMessageSocialSpyFormat();

    private String itemFormat = "<gray>[</gray><item><gray> x <amount>]";
    private String itemFormatInfo = "<dark_gray><item> x <amount>";

    private PMFormat mentionFormat = DefaultConfigObjects.createMentionFormat();
    private String mentionPrefix = "@";
    private String channelMentionFormat = "<yellow>";
    private Sound mentionSound = DefaultConfigObjects.createMentionSound();
    private boolean mentionOnMessage = true;

    public @NotNull PMFormat senderFormat() {
        return senderFormat;
    }

    public @NotNull PMFormat recipientFormat() {
        return recipientFormat;
    }

    public @NotNull PMFormat socialSpyFormat() {
        return socialSpyFormat;
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

    public @NotNull PMFormat mentionFormat() {
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
}
