package at.helpch.chatchat.config;

import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.format.Format;
import at.helpch.chatchat.api.format.PriorityFormat;
import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.config.holder.FormatsHolderImpl;
import at.helpch.chatchat.format.SimpleFormat;
import at.helpch.chatchat.format.ChatFormat;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Used to create default objects in the config
 */
public final class DefaultConfigObjects {

    public static @NotNull Channel createDefaultChannel() {
        return new ChatChannel("default", "",
            List.of("global"), "<gray>[<blue>Global<gray>]", new FormatsHolderImpl(), -1);
    }

    public static @NotNull Channel createStaffChannel() {
        return new ChatChannel("staff", "@",
            List.of("staffchat"), "<gray>[<green>Staff<gray>]", new FormatsHolderImpl(), -1);
    }

    public static @NotNull SimpleFormat createDefaultConsoleFormat() {
        final LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();

        map.put("channel", List.of("%chatchat_channel_prefix% "));
        map.put("prefix", List.of("<gray>[<color:#40c9ff>Chat<color:#e81cff>Chat<gray>] "));
        map.put("name", List.of("<white>%player_name%"));
        map.put("message", List.of(" <gray>» <white><message>"));
        return new SimpleFormat("console-format", map);
    }


    public static @NotNull PriorityFormat createDefaultFormat() {
        final LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();

        map.put("channel", List.of("%chatchat_channel_prefix% "));
        map.put("prefix", List.of("<gray>[<color:#40c9ff>Chat<color:#e81cff>Chat<gray>] "));
        map.put("name", List.of("<white>%player_name%"));
        map.put("message", List.of(" <gray>» <white><message>"));

        return new ChatFormat("default", 2, map);
    }

    public static @NotNull PriorityFormat createOtherFormat() {
        final LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();

        map.put("channel", List.of("%chatchat_channel_prefix% "));
        map.put("prefix", List.of("<gray>[<gradient:#40c9ff:#e81cff>ChatChat<gray>] "));
        map.put(
            "name",
            List.of(
                "<hover:show_text:\"Prefix: %vault_group%\">",
                "<rainbow>%player_name%",
                "</hover>"
            )
        );
        map.put("message", List.of(" <gray>» <white><message>"));

        return new ChatFormat("other", 1, map);
    }

    public static @NotNull SimpleFormat createPrivateMessageSenderFormat() {
        final LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();

        map.put("sender", List.of("<gray>you"));
        map.put("separator", List.of(" <color:#40c9ff>-> "));
        map.put("recipient", List.of("<gray><recipient:player_name>"));
        map.put("message", List.of(" <#e81cff>» <white><message>"));

        return new SimpleFormat("sender", map);
    }

    public static @NotNull SimpleFormat createPrivateMessageRecipientFormat() {
        final LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();

        map.put("sender", List.of("<gray>%player_name%"));
        map.put("separator", List.of(" <#40c9ff>-> "));
        map.put("recipient", List.of("<gray>you"));
        map.put("message", List.of(" <#e81cff>» <white><message>"));

        return new SimpleFormat("recipient", map);
    }

    public static @NotNull SimpleFormat createPrivateMessageSocialSpyFormat() {
        final LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();

        map.put("prefix", List.of("<gray>(spy) "));
        map.put("sender", List.of("%player_name%"));
        map.put("separator", List.of(" <#40c9ff>-> "));
        map.put("recipient", List.of("<gray><recipient:player_name>"));
        map.put("message", List.of(" <#e81cff>» <white><message>"));

        return new SimpleFormat("socialspy", map);
    }

    public static @NotNull SimpleFormat createPersonalMentionFormat() {
        final LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();

        map.put(
            "name",
            List.of(
                "<hover:show_text:\"<gold>This is a mention!\">",
                "<yellow>@%player_name%",
                "</hover>"
            )
        );

        return new SimpleFormat("personal-mention", map);
    }

    public static @NotNull SimpleFormat createChannelMentionFormat() {
        final LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();

        map.put(
            "name",
            List.of(
                "<hover:show_text:\"<gold>This is a mention!\">",
                "<yellow>@everyone",
                "</hover>"
            )
        );

        return new SimpleFormat("channel-mention", map);
    }

    public static @NotNull Sound createMentionSound() {
        return Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1f, 1f);
    }
}
