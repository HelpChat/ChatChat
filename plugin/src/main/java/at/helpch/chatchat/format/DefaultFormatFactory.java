package at.helpch.chatchat.format;

import org.jetbrains.annotations.NotNull;

import java.util.List;

final class DefaultFormatFactory {

    static @NotNull ChatFormat createDefaultFormat() {
        return ChatFormat.of(1,
                List.of("<gray>[</gray><color:#3dbbe4>Chat</color><color:#f3af4b>Chat</color><gray>]</gray> %player_name% » %message%"));
    }

    static @NotNull PMFormat createDefaultPrivateMessageSenderFormat() {
        return PMFormat.of(List.of("<gray>you <yellow> » <gray>%recipient_player_name% <gray>: %message%"));
    }

    static @NotNull PMFormat createDefaultPrivateMessageRecipientFormat() {
        return PMFormat.of(List.of("<gray>%player_name% <yellow> » <gray>you <gray>: %message%"));
    }
}
