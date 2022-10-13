package at.helpch.chatchat.placeholder;

import at.helpch.chatchat.api.placeholder.MiniPlaceholder;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import at.helpch.chatchat.util.MessageProcessor;
import at.helpch.chatchat.util.MessageUtils;
import at.helpch.chatchat.util.PapiTagUtils;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

// configurate requires non-final fields
@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public class MiniPlaceholderImpl implements MiniPlaceholder {

    private static final String MINI_PLACEHOLDER_PERMISSION = MessageProcessor.TAG_BASE_PERMISSION + "placeholder.";

    private final String name;
    private final boolean requiresRecipient;
    private final boolean parseMini;
    private final boolean parsePapi;
    private final boolean closing;
    private final String message;

    public MiniPlaceholderImpl(
        @NotNull final String name,
        final boolean requiresRecipient,
        final boolean parseMini,
        final boolean parsePapi,
        final boolean closing,
        @NotNull final String message
    ) {
        this.name = name;
        this.requiresRecipient = requiresRecipient;
        this.parseMini = parseMini;
        this.parsePapi = parsePapi;
        this.closing = closing;
        this.message = message;
    }

    public @NotNull TagResolver toTagResolver(
        final boolean inMessage,
        @NotNull final ChatUser sender,
        @NotNull User recipient
    ) {
        if (inMessage && !sender.player().hasPermission(MINI_PLACEHOLDER_PERMISSION + name)) {
            return TagResolver.empty();
        }

        if (!parseMini) {
            return Placeholder.unparsed(name, message);
        }

        final TagResolver papiTag;
        if (parsePapi) {
            if (requiresRecipient && recipient instanceof ChatUser) {
                papiTag = TagResolver.resolver(
                    PapiTagUtils.createPlaceholderAPITag(sender.player()),
                    PapiTagUtils.createRelPlaceholderAPITag(sender.player(), ((ChatUser) recipient).player()),
                    PapiTagUtils.createRecipientTag(((ChatUser) recipient).player())
                );
            } else {
                papiTag = PapiTagUtils.createPlaceholderAPITag(sender.player());
            }
        } else {
            papiTag = TagResolver.empty();
        }

        return closing
            ? Placeholder.component(name, MessageUtils.parseToMiniMessage(message, papiTag))
            : TagResolver.resolver(name, Tag.inserting(MessageUtils.parseToMiniMessage(message, papiTag)));
    }

    public @NotNull String name() {
        return name;
    }

    public boolean requiresRecipient() {
        return requiresRecipient;
    }

    public boolean parseMini() {
        return parseMini;
    }

    public boolean parsePapi() {
        return parsePapi;
    }

    public boolean closing() {
        return closing;
    }

    public @NotNull String message() {
        return message;
    }
}
