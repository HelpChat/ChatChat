package at.helpch.chatchat.processor;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.event.ChatChatEvent;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.user.ConsoleUser;
import at.helpch.chatchat.util.FormatUtils;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Pattern;

public final class MessageProcessor {

    private MessageProcessor() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static boolean processEvent(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final ChatUser sender,
        @NotNull final Channel channel,
        @NotNull final String message,
        final boolean async
    ) {
        if (!validateRules(plugin, sender, message)) {
            return false;
        }

        final var chatEvent = new ChatChatEvent(
            async,
            sender,
            FormatUtils.findFormat(sender.player(), channel, plugin.configManager().formats()),
            LocalToLocalMessageProcessor.processMessage(plugin, sender, ConsoleUser.INSTANCE, message),
            channel,
            channel.targets(sender)
        );

        plugin.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return false;
        }

        final boolean localResult = LocalToLocalMessageProcessor.processLocalMessageEvent(plugin, chatEvent, sender, channel, async);

        if (!localResult) {
            return false;
        }

        if (!channel.crossServer()) {
            return true;
        }

        LocalToRemoteMessageProcessor.processLocalMessageEvent(plugin, chatEvent, sender, channel);
        return true;
    }

    private static boolean validateRules(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final ChatUser sender,
        @NotNull final String message
    ) {
        final var rulesResult = plugin.ruleManager().isAllowedPublicChat(sender, message);
        if (rulesResult.isPresent()) {
            sender.sendMessage(rulesResult.get());
            return false;
        }

        return true;
    }

    public static final class Constants {
        public static final MiniMessage USER_MESSAGE_MINI_MESSAGE = MiniMessage.builder().tags(TagResolver.empty()).build();
        public static final Pattern DEFAULT_URL_PATTERN = Pattern.compile("(?:(https?)://)?([-\\w_.]+\\.\\w{2,})(/\\S*)?");
        public static final Pattern URL_SCHEME_PATTERN = Pattern.compile("^[a-z][a-z\\d+\\-.]*:");

        public static final TextReplacementConfig URL_REPLACER_CONFIG = TextReplacementConfig.builder()
            .match(DEFAULT_URL_PATTERN)
            .replacement(builder -> {
                String clickUrl = builder.content();
                if (!URL_SCHEME_PATTERN.matcher(clickUrl).find()) {
                    clickUrl = "https://" + clickUrl;
                }
                return builder.clickEvent(ClickEvent.openUrl(clickUrl));
            })
            .build();

        public static final String URL_PERMISSION = "chatchat.url";
        public static final String TAG_BASE_PERMISSION = "chatchat.tag.";
        public static final String ITEM_TAG_PERMISSION = TAG_BASE_PERMISSION + "item";

        public static final Map<String, TagResolver> PERMISSION_TAGS = Map.ofEntries(
            Map.entry("click", StandardTags.clickEvent()),
            Map.entry("color", StandardTags.color()),
            Map.entry("font", StandardTags.font()),
            Map.entry("gradient", StandardTags.gradient()),
            Map.entry("hover", StandardTags.hoverEvent()),
            Map.entry("insertion", StandardTags.insertion()),
            Map.entry("keybind", StandardTags.keybind()),
            Map.entry("newline", StandardTags.newline()),
            Map.entry("rainbow", StandardTags.rainbow()),
            Map.entry("reset", StandardTags.reset()),
            Map.entry("translatable", StandardTags.translatable())
        );

        private Constants() {
            throw new AssertionError("Util classes are not to be instantiated!");
        }
    }
}
