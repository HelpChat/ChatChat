package at.helpch.chatchat.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class MessageProcessorTagsTest {

    private static final MiniMessage LIMITED = MiniMessage.builder()
        .tags(net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.empty()).build();

    @Test
    public void spriteAndHeadRequireTheirOwnPermissions() {
        assertPermission("sprite", "<sprite:blocks:block/stone>");
        assertPermission("head", "<head:entity/player/wide/steve>");
    }

    @Test
    public void otherNewStandardTagsRequireTheirOwnPermissions() {
        assertPermission("shadow", "<shadow:red:0.5>text</shadow>");
        assertPermission("transition", "<transition:red:blue:0>text</transition>");
        assertPermission("translatable-fallback", "<lang_or:block.minecraft.stone:'Stone'>");
        assertPermission("pride", "<pride:trans>text</pride>");
    }

    @Test
    public void everyAddedTagIsPermissionGated() {
        final var tags = Map.of(
            "head", "head",
            "nbt", "nbt",
            "pride", "pride",
            "score", "score",
            "selector", "selector",
            "shadow", "shadow",
            "sprite", "sprite",
            "transition", "transition",
            "translatable-fallback", "lang_or"
        );
        final var denied = MessageProcessor.allowedStandardTags(granted -> false);

        tags.forEach((permission, tag) -> {
            final TagResolver allowed = MessageProcessor.allowedStandardTags(
                granted -> granted.equals(MessageProcessor.TAG_BASE_PERMISSION + permission));
            assertFalse(tag, denied.has(tag));
            assertTrue(tag, allowed.has(tag));
        });
    }

    private static void assertPermission(final String permission, final String input) {
        final var denied = LIMITED.deserialize(input, MessageProcessor.allowedStandardTags(granted -> false));
        final var allowed = LIMITED.deserialize(input, MessageProcessor.allowedStandardTags(
            granted -> granted.equals(MessageProcessor.TAG_BASE_PERMISSION + permission)));

        assertEquals(Component.text(input), denied);
        // Pride uses a virtual component whose equality is based on identity.
        if (!permission.equals("pride")) {
            assertEquals(MiniMessage.miniMessage().deserialize(input), allowed);
        }
        assertNotEquals(denied, allowed);
    }
}
