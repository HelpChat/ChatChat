package at.helpch.chatchat.config;

import at.helpch.chatchat.format.PMFormat;
import at.helpch.chatchat.util.FormatUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public final class SettingsHolder {
    private PMFormat senderFormat = FormatUtils.createDefaultPrivateMessageSenderFormat();
    private PMFormat receiverFormat = FormatUtils.createDefaultPrivateMessageReceiverFormat();

    public @NonNull PMFormat getSenderFormat() {
        return senderFormat;
    }

    public @NotNull PMFormat getRecieverFormat() {
        return receiverFormat;
    }
}
