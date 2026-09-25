package at.helpch.chatchat.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class ContextualComponentsTest {

    @Test
    public void recognizesComponentsThatNeedServerContextInsideFormattedMessages() {
        final var miniMessage = MiniMessage.miniMessage();

        assertTrue(ContextualComponents.needsResolution(
            Component.text("prefix ").append(miniMessage.deserialize("<selector:@e[limit=5]>"))));
        assertTrue(ContextualComponents.needsResolution(miniMessage.deserialize("<score:player:points>")));
        assertTrue(ContextualComponents.needsResolution(miniMessage.deserialize("<nbt:entity:'@s':Health/>")));
        assertTrue(ContextualComponents.needsResolution(
            miniMessage.deserialize("<hover:show_text:'<selector:@s>'>hover</hover>")));
        assertFalse(ContextualComponents.needsResolution(miniMessage.deserialize("<pride:trans>text</pride>")));
    }
}
