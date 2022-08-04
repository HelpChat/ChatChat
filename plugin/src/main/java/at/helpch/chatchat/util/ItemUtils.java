package at.helpch.chatchat.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ItemUtils {
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    private ItemUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    private static @NotNull Component getTranslation(@NotNull final Material material) {
        final var type = material.isBlock() ? "block" : "item";
        return Component.translatable(String.format("%s.minecraft.%s", type, material.getKey().getKey()));
    }

    public static @NotNull TagResolver.@NotNull Single createItemPlaceholder(
            @NotNull final String itemFormat,
            @NotNull final String itemFormatInfo,
            @NotNull final ItemStack item
    ) {
        final var materialName = getTranslation(item.getType());
        final var itemPlaceholder = Placeholder.component("item", materialName);
        final var amountPlaceholder = Placeholder.component("amount", Component.text(item.getAmount()));

        final var hoverInfoComponent = !itemFormatInfo.isBlank()
                ? MessageUtils.parseToMiniMessage(itemFormatInfo, itemPlaceholder, amountPlaceholder)
                : null;

        if (item.getType() == Material.AIR || item.getType() == Material.CAVE_AIR || item.getType() == Material.VOID_AIR || !item.hasItemMeta()) {
            return Placeholder.component(
                    "item",
                    MessageUtils.parseToMiniMessage(itemFormat, itemPlaceholder, amountPlaceholder).hoverEvent(hoverInfoComponent)
            );
        }

        final var meta = item.getItemMeta();

        // To get rid of IDE warnings
        if (meta == null) {
            return Placeholder.component(
                    "item",
                    MessageUtils.parseToMiniMessage(itemFormat, itemPlaceholder, amountPlaceholder).hoverEvent(hoverInfoComponent)
            );
        }

        final Component name = meta.hasDisplayName() ? LEGACY_COMPONENT_SERIALIZER.deserialize(meta.getDisplayName()) : materialName;

        final var newItemPlaceholder = Placeholder.component("item", name);

        final List<Component> enchants = meta.hasEnchants() && !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)
                ? meta.getEnchants()
                .entrySet()
                .stream()
                .map(entry -> formattedEnchantment(entry.getKey(), entry.getValue()).color(NamedTextColor.GRAY))
                .collect(Collectors.toList())
                : Collections.emptyList();

        final List<Component> lore = meta.hasLore()
                ? meta.getLore()
                .stream()
                .map(LEGACY_COMPONENT_SERIALIZER::deserialize)
                .map(it -> it.hasStyling() ? it : it.color(NamedTextColor.DARK_PURPLE))
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
                        Component.join(JoinConfiguration.newlines(), hoverComponents)
                )
        );
    }

    @SuppressWarnings("deprecation")
    private static @NotNull Component formattedEnchantment(@Nullable final Enchantment enchantment, @Nullable final Integer level) {
        if (enchantment == null) {
            return Component.empty();
        }

        final var isVanilla = enchantment.getKey().getNamespace().equals(NamespacedKey.MINECRAFT);
        final Component enchantmentName = isVanilla ? Component.translatable("enchantment.minecraft." + enchantment.getKey().getKey()) : Component.text(enchantment.getName());

        if (enchantment.getMaxLevel() == 1) {
            return enchantmentName;
        }

        if (level == null) {
            return enchantmentName.append(Component.text(" I"));
        }

        @NotNull final String roman;
        switch (level) {
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
                roman = level.toString();
                break;
        }

        return enchantmentName.append(Component.space()).append(Component.text(roman));
    }
}
