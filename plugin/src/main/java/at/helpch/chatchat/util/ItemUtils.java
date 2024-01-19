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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ItemUtils {

    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacySection();
    private static Method translationKeyMethod;
    private static final Map<Predicate<Material>, Function<Material, String>> translations = new LinkedHashMap<>();

    static {
        if (VersionHelper.IS_PAPER) {
            try {
                //noinspection JavaReflectionMemberAccess
                ItemUtils.translationKeyMethod = Material.class.getMethod("translationKey"); // paper method
            } catch (NoSuchMethodException ignored) {
            }
        }

        translations.put(
            __ -> VersionHelper.IS_PAPER && translationKeyMethod != null,
            material -> {
                try {
                    return (String) translationKeyMethod.invoke(material);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                    return "null." + material.getKey().getKey();
                }
            }
        );
        translations.put(
            material -> VersionHelper.HAS_SMITHING_TEMPLATE && material.name().endsWith("SMITHING_TEMPLATE"),
            __ -> "item.minecraft.smithing_template"
        );
        translations.put(
            Material::isItem,
            material -> "item.minecraft." + material.getKey().getKey()
        );
        translations.put(
            Material::isBlock,
            material -> "block.minecraft." + material.getKey().getKey()
        );
    }

    private ItemUtils() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    private static @NotNull Component getTranslation(@NotNull final Material material) {
        for (final var entry : translations.entrySet()) {
            if (entry.getKey().test(material)) {
                return Component.translatable(entry.getValue().apply(material));
            }
        }

        return Component.translatable("null." + material.getKey().getKey());
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
                ? MessageUtils.parseFromMiniMessage(itemFormatInfo, itemPlaceholder, amountPlaceholder)
                : null;

        if (item.getType().isAir() || !item.hasItemMeta()) {
            return Placeholder.component(
                    "item",
                    MessageUtils.parseFromMiniMessage(itemFormat, itemPlaceholder, amountPlaceholder).hoverEvent(hoverInfoComponent)
            );
        }

        final var meta = item.getItemMeta();

        // To get rid of IDE warnings
        if (meta == null) {
            return Placeholder.component(
                    "item",
                    MessageUtils.parseFromMiniMessage(itemFormat, itemPlaceholder, amountPlaceholder).hoverEvent(hoverInfoComponent)
            );
        }

        final Component name = meta.hasDisplayName() ? LEGACY_COMPONENT_SERIALIZER.deserialize(meta.getDisplayName()) : materialName;

        final var newItemPlaceholder = Placeholder.component("item", name);

        var enchants = Collections.<Component>emptyList();

        if (meta.hasEnchants() && !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
            enchants = meta.getEnchants().entrySet()
                .stream()
                .map(entry -> formattedEnchantment(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        }

        var lore = Collections.<Component>emptyList();

        if (meta.hasLore()) {
            //noinspection ConstantConditions
            lore = meta.getLore()
                .stream()
                .map(LEGACY_COMPONENT_SERIALIZER::deserialize)
                .map(it -> it.hasStyling() ? it : it.color(NamedTextColor.DARK_PURPLE))
                .collect(Collectors.toList());
        }

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
                MessageUtils.parseFromMiniMessage(itemFormat, newItemPlaceholder, amountPlaceholder).hoverEvent(
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
        Component enchantmentName = isVanilla ? Component.translatable("enchantment.minecraft." + enchantment.getKey().getKey()) : Component.text(enchantment.getName());

        if (!enchantmentName.hasStyling()) {
            enchantmentName = enchantmentName.color(NamedTextColor.GRAY);
        }

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
