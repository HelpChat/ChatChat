package at.helpch.chatchat.config;

import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.format.ChatFormat;
import at.helpch.chatchat.format.PMFormat;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Used to create default objects in the config
 */
public final class DefaultConfigObjects {

    public static @NotNull ChatChannel createDefaultChannel() {
        return new ChatChannel("default",
                "", "global", "<gray>[<blue>Global<gray>]");
    }

    public static @NotNull ChatChannel createStaffChannel() {
        return new ChatChannel("staff", "@", "staffchat", "<gray>[<green>Staff<gray>]");
    }

    public static @NotNull ChatFormat createDefaultFormat() {
        return new ChatFormat("default", 2,
                List.of(
                        "%chatchat_channel_prefix% ",
                        "<gray>[<color:#40c9ff>Chat<color:#e81cff>Chat<gray>] ",
                        "<white>%player_name% ",
                        "<gray>» ",
                        "<white><message>"));
    }

    public static @NotNull ChatFormat createOtherFormat() {
        return new ChatFormat("other", 1,
                List.of(
                        "%chatchat_channel_prefix% ",
                        "<hover:show_text:\"Prefix: %vault_group%\"><gray>[<gradient:#40c9ff:#e81cff>ChatChat<gray>] ",
                        "<rainbow>%player_name% ",
                        "<gray>» ",
                        "<white><message>"));
    }

    public static @NotNull PMFormat createPrivateMessageSenderFormat() {
        return new PMFormat("sender", List.of("<gray>you <color:#40c9ff>-> <gray>%recipient_player_name% <#e81cff>» <white><message>"));
    }

    public static @NotNull PMFormat createPrivateMessageRecipientFormat() {
        return new PMFormat("recipient", List.of("<gray>%player_name% <#40c9ff>-> <gray>you <#e81cff>» <white><message>"));
    }

    public static @NotNull PMFormat createPrivateMessageSocialSpyFormat() {
        return new PMFormat("socialspy", List.of("<gray>(spy) %player_name% <#40c9ff>-> <gray>%recipient_player_name% <#e81cff>» <white><message>"));
    }
}
