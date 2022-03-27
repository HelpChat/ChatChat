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

    private String mentionPrefix = "@";
    private Sound mentionSound = DefaultConfigObjects.createMentionSound();
    private boolean mentionOnMessage = true;

    public @NotNull PMFormat getSenderFormat() {
        return senderFormat;
    }

    public @NotNull PMFormat getRecipientFormat() {
        return recipientFormat;
    }

    public @NotNull PMFormat getSocialSpyFormat() {
        return socialSpyFormat;
    }

    public @NotNull String getMentionPrefix() {
        return mentionPrefix;
    }

    public @NotNull Sound getMentionSound() {
        return mentionSound;
    }

    public boolean getMentionOnMessage() {
        return mentionOnMessage;
    }
}
