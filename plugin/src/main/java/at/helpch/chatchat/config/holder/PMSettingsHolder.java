package at.helpch.chatchat.config.holder;

import at.helpch.chatchat.api.format.SimpleFormat;
import at.helpch.chatchat.config.DefaultConfigObjects;
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

        public @NotNull SimpleFormat senderFormat() {
            return senderFormat;
        }

        public @NotNull SimpleFormat recipientFormat() {
            return recipientFormat;
        }

        public @NotNull SimpleFormat socialSpyFormat() {
            return socialSpyFormat;
        }
    }
}
