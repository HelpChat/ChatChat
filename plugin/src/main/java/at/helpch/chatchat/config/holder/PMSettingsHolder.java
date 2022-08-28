package at.helpch.chatchat.config.holder;

import at.helpch.chatchat.config.DefaultConfigObjects;
import at.helpch.chatchat.format.BasicFormat;
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
        private BasicFormat senderFormat = DefaultConfigObjects.createPrivateMessageSenderFormat();
        private BasicFormat recipientFormat = DefaultConfigObjects.createPrivateMessageRecipientFormat();
        private BasicFormat socialSpyFormat = DefaultConfigObjects.createPrivateMessageSocialSpyFormat();

        public @NotNull BasicFormat senderFormat() {
            return senderFormat;
        }

        public @NotNull BasicFormat recipientFormat() {
            return recipientFormat;
        }

        public @NotNull BasicFormat socialSpyFormat() {
            return socialSpyFormat;
        }
    }
}
