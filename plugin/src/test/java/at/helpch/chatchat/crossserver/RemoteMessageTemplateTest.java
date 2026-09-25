package at.helpch.chatchat.crossserver;

import at.helpch.chatchat.mention.MentionResultImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class RemoteMessageTemplateTest {

    @Test
    public void processesOnlyTheMessageWithoutChangingItsGradientOrRainbow() {
        final var marker = "chatchat:message/test";
        final var message = Component.text("@Alice");
        final var markedMessage = Component.text().insertion(marker).append(message).build();

        for (final var format : new String[] {
            "<gradient:red:blue>@Alice prefix <message> suffix</gradient>",
            "<rainbow>@Alice prefix <message> suffix</rainbow>"
        }) {
            final var formatted = MiniMessage.miniMessage().deserialize(format,
                Placeholder.component("message", markedMessage));
            final var received = GsonComponentSerializer.gson().deserialize(
                GsonComponentSerializer.gson().serialize(formatted));
            final var untouched = RemoteMessageTemplate.process(received, marker,
                body -> new MentionResultImpl(body, false, false));
            final var expected = MiniMessage.miniMessage().deserialize(format,
                Placeholder.component("message", message));

            assertEquals(GsonComponentSerializer.gson().serialize(expected),
                GsonComponentSerializer.gson().serialize(untouched.component()));

            final var mentioned = RemoteMessageTemplate.process(received, marker,
                body -> new MentionResultImpl(Component.text("PING"), true, true));
            assertEquals("@Alice prefix PING suffix", plain(mentioned.component()));
            assertTrue(mentioned.playSound());
            assertFalse(GsonComponentSerializer.gson().serialize(mentioned.component()).contains(marker));
        }
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
