package at.helpch.chatchat.format;

import at.helpch.chatchat.api.Format;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;

@ConfigSerializable
public final class PMFormat implements Format {

    public static transient final PMFormat DEFAULT_SENDER_FORMAT = DefaultFormatFactory.createDefaultPrivateMessageSenderFormat();
    public static transient final PMFormat DEFAULT_RECEIVER_FORMAT = DefaultFormatFactory.createDefaultPrivateMessageReceiverFormat();
    private List<String> parts = Collections.emptyList();

    // constructor for Configurate
    public PMFormat() {}

    private PMFormat(@NotNull final List<String> parts) {
        this.parts = parts;
    }

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public @NotNull Format priority(final int priority) {
        return this;
    }

    @Override
    public @NotNull List<String> parts() {
        return parts;
    }

    @Override
    public @NotNull PMFormat parts(@NotNull final List<String> parts) {
        return of(parts);
    }

    public static @NotNull PMFormat of(@NotNull final List<String> parts) {
        return new PMFormat(parts);
    }

    @Override
    public String toString() {
        return "PMFormat{" +
                "priority=" + priority() +
                ", parts=" + parts +
                '}';
    }
}
