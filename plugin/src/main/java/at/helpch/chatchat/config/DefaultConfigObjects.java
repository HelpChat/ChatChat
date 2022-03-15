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
final class DefaultConfigObjects {

    static @NotNull ChatChannel createDefaultChannel() {
        return ChatChannel.of(
                "", "global", "<gray>[<blue>Global<gray>]", Collections.emptyList());
    }

    static @NotNull ChatChannel createStaffChannel() {
        return ChatChannel.of("@", "staffchat", "<gray>[<green>Staff<gray>]", Collections.emptyList());
    }

    static @NotNull ChatFormat createDefaultFormat() {
        return ChatFormat.of(2,
                List.of(
                        "%chatchat_channel_prefix% ",
                        "<gray>[<color:#40c9ff>Chat<color:#e81cff>Chat<gray>] ",
                        "<white>%player_name% ",
                        "<gray>» ",
                        "<white><message>"));
    }

    static @NotNull ChatFormat createOtherFormat() {
        return ChatFormat.of(1,
                List.of(
                        "%chatchat_channel_prefix% ",
                        "<hover:show_text:\"Prefix: %vault_group%\"><gray>[<gradient:#40c9ff:#e81cff>ChatChat<gray>] ",
                        "<rainbow>%player_name% ",
                        "<gray>» ",
                        "<white><message>"));
    }

    static @NotNull PMFormat createPrivateMessageSenderFormat() {
        return PMFormat.of(List.of("<gray>you <color:#40c9ff>-> <gray>%recipient_player_name% <#e81cff>» <white><message>"));
    }

    static @NotNull PMFormat createPrivateMessageRecipientFormat() {
        return PMFormat.of(List.of("<gray>%player_name% <#40c9ff>-> <gray>you <#e81cff>» <white><message>"));
    }
}
