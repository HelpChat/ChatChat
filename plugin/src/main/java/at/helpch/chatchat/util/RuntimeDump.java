package at.helpch.chatchat.util;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.format.Format;
import at.helpch.chatchat.api.format.PriorityFormat;
import at.helpch.chatchat.api.hook.Hook;
import at.helpch.chatchat.api.placeholder.MiniPlaceholder;
import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.format.ChatFormat;
import at.helpch.chatchat.placeholder.MiniPlaceholderImpl;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** A snapshot of the objects ChatChat is using, separate from the source YAML in a dump. */
final class RuntimeDump {

    private static final Gson GSON = new Gson();

    private RuntimeDump() {
        throw new AssertionError("Util classes should not be initialized");
    }

    static void appendAll(@NotNull final ChatChatPlugin plugin, @NotNull final StringBuilder builder) {
        heading(builder, "Loaded runtime state");
        appendChannelTypes(plugin, builder);
        appendChannels(builder, plugin.configManager().channels().channels(),
            plugin.configManager().channels().defaultChannel(), ChatChannel.defaultChannel());
        appendFormats(builder, plugin.configManager().formats().formats(),
            plugin.configManager().formats().defaultFormat(), ChatFormat.defaultFormat());
        appendConsoleFormat(plugin, builder);
        appendPrivateMessageFormats(plugin, builder);
        appendPlaceholders(plugin, builder);
        appendHooks(plugin, builder);
        appendClasses(builder, "Registered mentions", plugin.mentionsManager().mentions());
        appendClasses(builder, "Registered public chat rules", plugin.ruleManager().publicChatRules());
        appendClasses(builder, "Registered private chat rules", plugin.ruleManager().privateChatRules());
    }

    static void appendForFile(@NotNull final ChatChatPlugin plugin, @NotNull final StringBuilder builder,
                              @NotNull final String fileName) {
        switch (fileName) {
            case "channels.yml" -> {
                heading(builder, "Loaded channel state");
                appendChannelTypes(plugin, builder);
                appendChannels(builder, plugin.configManager().channels().channels(),
                    plugin.configManager().channels().defaultChannel(), ChatChannel.defaultChannel());
            }
            case "formats.yml" -> {
                heading(builder, "Loaded format state");
                appendFormats(builder, plugin.configManager().formats().formats(),
                    plugin.configManager().formats().defaultFormat(), ChatFormat.defaultFormat());
                appendConsoleFormat(plugin, builder);
            }
            case "settings.yml" -> {
                heading(builder, "Loaded private message formats");
                appendPrivateMessageFormats(plugin, builder);
            }
            default -> { }
        }
    }

    private static void appendChannelTypes(final ChatChatPlugin plugin, final StringBuilder builder) {
        final var names = plugin.channelTypeRegistry().builders().keySet().stream().sorted().toList();
        line(builder, "Registered channel types (" + names.size() + "): " + GSON.toJson(names));
    }

    static void appendChannels(final StringBuilder builder, final Map<String, Channel> channels,
                               final String configuredDefault, final Channel activeDefault) {
        line(builder, "Configured default channel: " + quote(configuredDefault));
        line(builder, "Active default channel: " + quote(activeDefault.name()) + " ("
            + activeDefault.getClass().getName() + ", registered="
            + channels.values().stream().anyMatch(channel -> channel == activeDefault) + ")");
        line(builder, "Registered channels (" + channels.size() + "):");
        channels.entrySet().stream().sorted(Map.Entry.comparingByKey())
            .forEach(entry -> appendChannel(builder, entry.getKey(), entry.getValue(),
                entry.getValue() == activeDefault));
        if (channels.values().stream().noneMatch(channel -> channel == activeDefault)) {
            line(builder, "Internal fallback channel:");
            appendChannel(builder, activeDefault.name(), activeDefault, true);
        }
        line(builder, "");
    }

    private static void appendChannel(final StringBuilder builder, final String key, final Channel channel,
                                      final boolean isDefault) {
        line(builder, "  - key=" + quote(key) + ", name=" + quote(channel.name())
            + ", type=" + channel.getClass().getName() + ", default=" + isDefault);
        line(builder, "    message-prefix=" + quote(channel.messagePrefix())
            + ", channel-prefix=" + quote(channel.channelPrefix()));
        line(builder, "    commands=" + GSON.toJson(channel.commandNames())
            + ", radius=" + channel.radius() + ", cross-server=" + channel.crossServer());
        appendPriorityFormats(builder, channel.formats().formats(), "    Channel formats", null);
    }

    static void appendFormats(final StringBuilder builder, final Map<String, PriorityFormat> formats,
                              final String configuredDefault, final PriorityFormat activeDefault) {
        line(builder, "Configured default format: " + quote(configuredDefault));
        line(builder, "Active default format: " + quote(activeDefault.name()) + " ("
            + activeDefault.getClass().getName() + ", registered="
            + formats.values().stream().anyMatch(format -> format == activeDefault) + ")");
        appendPriorityFormats(builder, formats, "Registered global formats", activeDefault);
        if (formats.values().stream().noneMatch(format -> format == activeDefault)) {
            line(builder, "Internal fallback format:");
            appendFormat(builder, activeDefault.name(), activeDefault, true, "  ");
        }
        line(builder, "");
    }

    private static void appendPriorityFormats(final StringBuilder builder, final Map<String, PriorityFormat> formats,
                                              final String title, final PriorityFormat activeDefault) {
        line(builder, title + " (" + formats.size() + "):");
        formats.entrySet().stream().sorted(Map.Entry.comparingByKey())
            .forEach(entry -> appendFormat(builder, entry.getKey(), entry.getValue(),
                activeDefault == null ? null : entry.getValue() == activeDefault, "  "));
    }

    private static void appendFormat(final StringBuilder builder, final String key, final Format format,
                                     final Boolean isDefault, final String indent) {
        final var priority = format instanceof PriorityFormat priorityFormat
            ? ", priority=" + priorityFormat.priority() : "";
        final var defaultValue = isDefault == null ? "" : ", default=" + isDefault;
        line(builder, indent + "- key=" + quote(key) + ", name=" + quote(format.name())
            + ", type=" + format.getClass().getName() + priority + defaultValue);
        line(builder, indent + "  parts (in render order):");
        format.parts().forEach((part, lines) ->
            line(builder, indent + "    " + quote(part) + " = " + GSON.toJson(lines)));
    }

    private static void appendConsoleFormat(final ChatChatPlugin plugin, final StringBuilder builder) {
        line(builder, "Loaded console format:");
        appendFormat(builder, "console", plugin.configManager().formats().consoleFormat(), null, "  ");
        line(builder, "");
    }

    private static void appendPrivateMessageFormats(final ChatChatPlugin plugin, final StringBuilder builder) {
        line(builder, "Loaded private message formats:");
        final var pmFormats = plugin.configManager().settings().privateMessagesSettings().formats();
        appendFormat(builder, "private-message-sender", pmFormats.senderFormat(), null, "  ");
        appendFormat(builder, "private-message-recipient", pmFormats.recipientFormat(), null, "  ");
        appendFormat(builder, "private-message-social-spy", pmFormats.socialSpyFormat(), null, "  ");
        line(builder, "");
    }

    private static void appendPlaceholders(final ChatChatPlugin plugin, final StringBuilder builder) {
        final Set<MiniPlaceholder> placeholders = plugin.miniPlaceholdersManager().placeholders();
        line(builder, "Registered MiniPlaceholders (" + placeholders.size() + "):");
        placeholders.stream().sorted(Comparator.comparing(RuntimeDump::placeholderKey)).forEach(placeholder -> {
            if (placeholder instanceof MiniPlaceholderImpl configured) {
                line(builder, "  - name=" + quote(configured.name()) + ", type=" + configured.getClass().getName()
                    + ", requires-recipient=" + configured.requiresRecipient()
                    + ", parse-mini=" + configured.parseMini() + ", parse-papi=" + configured.parsePapi()
                    + ", closing=" + configured.closing());
            } else {
                line(builder, "  - type=" + placeholder.getClass().getName());
            }
        });
        line(builder, "");
    }

    private static String placeholderKey(final MiniPlaceholder placeholder) {
        return placeholder instanceof MiniPlaceholderImpl configured ? configured.name()
            : placeholder.getClass().getName();
    }

    private static void appendHooks(final ChatChatPlugin plugin, final StringBuilder builder) {
        final List<Hook> hooks = new ArrayList<>();
        hooks.addAll(plugin.hookManager().hooks());
        hooks.addAll(plugin.hookManager().vanishHooks());
        hooks.addAll(plugin.hookManager().muteHooks());
        hooks.sort(Comparator.comparing(Hook::name));
        line(builder, "Registered hooks (" + hooks.size() + "):");
        hooks.forEach(hook -> line(builder, "  - name=" + quote(hook.name()) + ", provider="
            + quote(hook.plugin().getName()) + ", type=" + hook.getClass().getName()));
        line(builder, "");
    }

    private static void appendClasses(final StringBuilder builder, final String title, final Set<?> values) {
        line(builder, title + " (" + values.size() + "):");
        values.stream().map(value -> value.getClass().getName()).sorted()
            .forEach(name -> line(builder, "  - " + name));
        line(builder, "");
    }

    private static void heading(final StringBuilder builder, final String title) {
        line(builder, "---------------------------------------------");
        line(builder, title);
        line(builder, "---------------------------------------------");
    }

    private static String quote(final String value) {
        return GSON.toJson(value);
    }

    private static void line(final StringBuilder builder, final String value) {
        builder.append(value).append(System.lineSeparator());
    }
}
