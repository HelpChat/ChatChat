package at.helpch.chatchat.config;

import at.helpch.chatchat.format.PMFormat;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public final class SettingsHolder {
    private PMFormat senderFormat = PMFormat.DEFAULT_SENDER_FORMAT;
    private PMFormat receiverFormat = PMFormat.DEFAULT_RECEIVER_FORMAT;

    public @NonNull PMFormat getSenderFormat() {
        return senderFormat;
    }

    public @NotNull PMFormat getRecieverFormat() {
        return receiverFormat;
    }
}
