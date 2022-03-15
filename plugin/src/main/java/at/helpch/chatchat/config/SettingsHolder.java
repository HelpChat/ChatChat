package at.helpch.chatchat.config;

import at.helpch.chatchat.format.PMFormat;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public final class SettingsHolder {

    private PMFormat senderFormat = DefaultConfigObjects.createPrivateMessageSenderFormat();

    private PMFormat recipientFormat = DefaultConfigObjects.createPrivateMessageRecipientFormat();

    public @NotNull PMFormat getSenderFormat() {
        return senderFormat;
    }

    public @NotNull PMFormat getRecipientFormat() {
        return recipientFormat;
    }
}
