package at.helpch.chatchat.util;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.event.ChatChatEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MessageProcessor {
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    private static final Pattern DEFAULT_URL_PATTERN = Pattern.compile("(?:(https?)://)?([-\\w_.]+\\.\\w{2,})(/\\S*)?");
    private static final Pattern URL_SCHEME_PATTERN = Pattern.compile("^[a-z][a-z0-9+\\-.]*:");

    private static final TextReplacementConfig URL_REPLACER_CONFIG = TextReplacementConfig.builder()
        .match(DEFAULT_URL_PATTERN)
        .replacement(builder -> {
            String clickUrl = builder.content();
            if (!URL_SCHEME_PATTERN.matcher(clickUrl).find()) {
                clickUrl = "https://" + clickUrl;
            }
            return builder.clickEvent(ClickEvent.openUrl(clickUrl));
        })
        .build();

    private static final String URL_PERMISSION = "chatchat.url";
    private static final String UTF_PERMISSION = "chatchat.utf";
    private static final String MENTION_PERMISSION = "chatchat.mention";
    private static final String MENTION_EVERYONE_PERMISSION = "chatchat.mention.everyone";
    private static final String TAG_BASE_PERMISSION = "chatchat.tag.";
    private static final String ITEM_PERMISSION = TAG_BASE_PERMISSION + "item";

    private static final Map<String, TagResolver> PERMISSION_TAGS = Map.ofEntries(
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

    public static void process(
        @NotNull final ChatChatPlugin plugin,
        @NotNull final ChatUser user,
        @NotNull final Channel channel,
        @NotNull final String message,
        final boolean async
    ) {
        if (StringUtils.containsIllegalChars(message) && !user.player().hasPermission(UTF_PERMISSION)) {
            user.sendMessage(Component.text("You can't use special characters in chat!", NamedTextColor.RED));
            return;
        }

        final var resolver = TagResolver.builder();

        for (final var entry : PERMISSION_TAGS.entrySet()) {
            if (!user.player().hasPermission(TAG_BASE_PERMISSION + entry.getKey())) {
                continue;
            }

            resolver.resolver(entry.getValue());
        }

        for (final var tag : TextDecoration.values()) {
            if (!user.player().hasPermission(TAG_BASE_PERMISSION + tag.toString())) {
                continue;
            }

            resolver.resolver(StandardTags.decorations(tag));
        }

        if (user.player().hasPermission(ITEM_PERMISSION)) {
            resolver.resolver(
                createItemPlaceholder(
                    plugin.configManager().settings().itemFormat(),
                    plugin.configManager().settings().itemFormatInfo(),
                    user.player().getInventory().getItemInMainHand()
                )
            );
        }

        final var miniMessage = MiniMessage.builder().tags(resolver.build()).build();
        final var deserializedMessage = !user.player().hasPermission(URL_PERMISSION)
            ? miniMessage.deserialize(message)
            : miniMessage.deserialize(message).replaceText(URL_REPLACER_CONFIG);

        final var format = FormatUtils.findFormat(user.player(), plugin.configManager().formats());

        final var chatEvent = new ChatChatEvent(
            async,
            user,
            format,
            deserializedMessage,
            channel
        );

        plugin.getServer().getPluginManager().callEvent(chatEvent);

        if (chatEvent.isCancelled()) {
            return;
        }

        final var oldChannel = user.channel();
        user.channel(channel);
        var component = FormatUtils.parseFormat(
            chatEvent.format(),
            user.player(),
            chatEvent.message()
        );

        final var mentionPrefix = plugin.configManager().settings().mentionPrefix();
        final var mentionSound = plugin.configManager().settings().mentionSound();
        final var mentionFormat = plugin.configManager().settings().mentionFormat();
        var mentionEveryone = false;

        if (user.player().hasPermission(MENTION_EVERYONE_PERMISSION)) {
            final var replaced = MentionUtils.replaceMention(mentionPrefix + "(everyone|here|channel)",
                    component, plugin.configManager().settings().globalMentionFormat());
            component = replaced.component();
            mentionEveryone = replaced.didReplace();
        }

        for (final var target : channel.targets(user)) {
            var mention = mentionEveryone;
            var transformedComponent = component;
            if (target instanceof ChatUser && user.player().hasPermission(MENTION_PERMISSION)) {
                final var replaced = MentionUtils.replaceMention((ChatUser) target, mentionPrefix,
                        component, mentionFormat);
                mention = replaced.didReplace() || mention;
                transformedComponent = replaced.component();
            }
            if (mention) target.playSound(mentionSound);
            target.sendMessage(transformedComponent);
        }
        user.channel(oldChannel);
    }

    private static @NotNull TagResolver.@NotNull Single createItemPlaceholder(
        @NotNull final String itemFormat,
        @NotNull final String itemFormatInfo,
        @NotNull final ItemStack item
    ) {
        final var itemPlaceholder = Placeholder.component(
            "item",
            Component.text(item.getType().name().toLowerCase(Locale.getDefault()))
        );

        final var amountPlaceholder = Placeholder.component(
            "amount",
            Component.text(item.getAmount())
        );

        final var hoverInfoComponent = !itemFormatInfo.isBlank()
            ? MessageUtils.parseToMiniMessage(itemFormatInfo, itemPlaceholder, amountPlaceholder)
            : null;

        if (item.getType().isAir() || !item.hasItemMeta()) {
            return Placeholder.component(
                "item",
                MessageUtils.parseToMiniMessage(itemFormat, itemPlaceholder, amountPlaceholder).hoverEvent(hoverInfoComponent));
        }

        final var meta = item.getItemMeta();

        final var name = LEGACY_COMPONENT_SERIALIZER.deserialize(
            meta.hasDisplayName()
                ? meta.getDisplayName()
                : meta.hasLocalizedName()
                ? meta.getLocalizedName()
                : item.getType().name().toLowerCase(Locale.getDefault())
        );

        final var newItemPlaceholder = Placeholder.component("item", name);

        final List<Component> enchants = meta.hasEnchants() && !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)
            ? meta.getEnchants()
            .entrySet()
            .stream()
            .map(entry -> "&7" + formattedEnchantment(entry))
            .map(LEGACY_COMPONENT_SERIALIZER::deserialize)
            .collect(Collectors.toList())
            : Collections.emptyList();

        final List<Component> lore = meta.hasLore()
            ? meta.getLore()
            .stream()
            .map(LEGACY_COMPONENT_SERIALIZER::deserialize)
            .map(textComponent -> {
                if (!textComponent.hasStyling()) {
                    return textComponent.color(NamedTextColor.DARK_PURPLE);
                }
                return textComponent;
            })
            .collect(Collectors.toList())
            : Collections.emptyList();

        final var hoverComponents = new ArrayList<Component>();

        hoverComponents.add(name);
        hoverComponents.addAll(enchants);
        hoverComponents.addAll(lore);

        if (hoverInfoComponent != null) {
            hoverComponents.add(Component.empty());
            hoverComponents.add(hoverInfoComponent);
        }

        return Placeholder.component(
            "item",
            MessageUtils.parseToMiniMessage(itemFormat, newItemPlaceholder, amountPlaceholder).hoverEvent(
                hoverComponents.stream().collect(Component.toComponent(Component.newline()))
            )
        );
    }

    private static @NotNull String formattedEnchantment(@NotNull final Map.Entry<Enchantment, Integer> entry) {
        final var enchantment = entry.getKey();
        final var value = entry.getValue();

        if (enchantment == null) {
            return "";
        }

        final var key = enchantment.getKey().getKey();

        final var enchantmentName = key.substring(0, 1).toUpperCase(Locale.getDefault())
            + key.substring(1);

        if (enchantment.getMaxLevel() == 1) {
            return enchantmentName;
        }

        if (value == null) {
            return enchantmentName + " I";
        }

        @NotNull final String roman;
        switch (value) {
            case 1:
                roman = "I";
                break;
            case 2:
                roman = "II";
                break;
            case 3:
                roman = "III";
                break;
            case 4:
                roman = "IV";
                break;
            case 5:
                roman = "V";
                break;
            default:
                roman = value.toString();
                break;
        }

        return enchantmentName + " " + roman;
    }
}
