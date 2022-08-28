package at.helpch.chatchat.format;

import at.helpch.chatchat.api.PriorityFormat;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;

public final class DefaultFormatFactory {

    /*
    This is only used as an internal format to send when a user:
    A - doesn't have any format permissions
    B - The default-format config option isn't set correctly
     */
    public static @NotNull PriorityFormat createDefaultFormat() {
        final LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();

        map.put("prefix", List.of("<gray>[<color:#40c9ff>Chat<color:#e81cff>Chat<gray>] "));
        map.put("name", List.of("<white>%player_name%"));
        map.put("message", List.of(" <gray>» <white><message>"));

        return new ChatFormat("default", 2, map);
    }

}
