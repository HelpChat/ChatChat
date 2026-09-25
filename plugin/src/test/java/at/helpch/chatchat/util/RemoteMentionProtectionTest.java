package at.helpch.chatchat.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class RemoteMentionProtectionTest {

    @Test
    public void matchesColoredMentionButNotItemNameAcrossServers() {
        final var foreignMarker = "\u0000chatchat:item:foreign:";
        final var message = Component.text("hi ")
            .append(Component.text("@Al", NamedTextColor.RED))
            .append(Component.text("ice", NamedTextColor.BLUE))
            .append(Component.text(" and "))
            .append(Component.text("@Alice").insertion(foreignMarker + "0"));
        final var incoming = MentionProtection.rebase(GsonComponentSerializer.gson().deserialize(
            GsonComponentSerializer.gson().serialize(message)), foreignMarker);
        final var result = MentionUtils.replaceMention("(?<![A-Za-z0-9_])@Alice(?![A-Za-z0-9_])",
            incoming, match -> Component.text("PING"));

        assertTrue(result.didReplace());
        assertEquals("hi PING and @Alice", plain(MentionProtection.restore(result.component())));
    }

    private static String plain(final Component component) {
        final var text = new StringBuilder();
        if (component instanceof TextComponent) {
            text.append(((TextComponent) component).content());
        }
        component.children().forEach(child -> text.append(plain(child)));
        return text.toString();
    }
}
