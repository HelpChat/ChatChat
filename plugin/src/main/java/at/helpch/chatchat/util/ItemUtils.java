package at.helpch.chatchat.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class ItemUtils {
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    private ItemUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    public static @NotNull TagResolver.@NotNull Single createItemPlaceholder(
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
