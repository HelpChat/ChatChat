package at.helpch.chatchat.format;

import org.jetbrains.annotations.NotNull;

import java.util.List;

final class DefaultFormatFactory {

    /*
    This is only used as an internal format to send when a user:
    A - doesn't have any format permissions
    B - The default-format config option isn't set correctly
     */
    static @NotNull ChatFormat createDefaultFormat() {
        return ChatFormat.of(1,
                List.of("<gray>[<color:#40c9ff>Chat<color:#e81cff>Chat<gray>] %player_name% » %message%"));
    }

}
