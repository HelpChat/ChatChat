package at.helpch.chatchat.config.holder;

import at.helpch.chatchat.api.format.Format;
import at.helpch.chatchat.config.DefaultConfigObjects;
import at.helpch.chatchat.format.SimpleFormat;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

// configurate requires non-final fields
@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public final class PMSettingsHolder {

    private boolean enabled = true;

    private PMFormats formats = new PMFormats();

    public boolean enabled() {
        return enabled;
    }

    public PMFormats formats() {
        return formats;
    }

    // configurate requires non-final fields
    @SuppressWarnings("FieldMayBeFinal")
    @ConfigSerializable
    public static final class PMFormats {
        private SimpleFormat senderFormat = DefaultConfigObjects.createPrivateMessageSenderFormat();
        private SimpleFormat recipientFormat = DefaultConfigObjects.createPrivateMessageRecipientFormat();
        private SimpleFormat socialSpyFormat = DefaultConfigObjects.createPrivateMessageSocialSpyFormat();

        public @NotNull Format senderFormat() {
            return senderFormat;
        }

        public @NotNull Format recipientFormat() {
            return recipientFormat;
        }

        public @NotNull Format socialSpyFormat() {
            return socialSpyFormat;
        }
    }
}
