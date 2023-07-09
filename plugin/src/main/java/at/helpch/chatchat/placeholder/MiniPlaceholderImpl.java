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
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.Optional;

// configurate requires non-final fields
@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public class MiniPlaceholderImpl implements MiniPlaceholder {

    private static final String MINI_PLACEHOLDER_PERMISSION = MessageProcessor.TAG_BASE_PERMISSION + "placeholder.";

    @Setting("name")
    private final String tagName;
    @Setting("requires-recipient")
    private final boolean isRelationalTag;
    @Setting("parse-mini")
    private final boolean shouldParseMiniMessageTags;
    @Setting("parse-papi")
    private final boolean shouldParsePlaceholderAPIPlaceholders;
    @Setting("closing")
    private final boolean shouldAutoCloseTags;
    @Setting("message")
    private final String message;

    public MiniPlaceholderImpl(
        @NotNull final String tagName,
        final boolean isRelationalTag,
        final boolean shouldParseMiniMessageTags,
        final boolean shouldParsePlaceholderAPIPlaceholders,
        final boolean shouldAutoCloseTags,
        @NotNull final String message
    ) {
        this.tagName = tagName;
        this.isRelationalTag = isRelationalTag;
        this.shouldParseMiniMessageTags = shouldParseMiniMessageTags;
        this.shouldParsePlaceholderAPIPlaceholders = shouldParsePlaceholderAPIPlaceholders;
        this.shouldAutoCloseTags = shouldAutoCloseTags;
        this.message = message;
    }

    public @NotNull TagResolver toTagResolver(final @NotNull Context context) {
        final Optional<ChatUser> sender = context.sender();
        final Optional<User> recipient = context.recipient();

        if (sender.isEmpty()) {
            return TagResolver.empty();
        }

        if (context.inMessage() && !sender.get().player().hasPermission(MINI_PLACEHOLDER_PERMISSION + tagName)) {
            return TagResolver.empty();
        }

        if (!shouldParseMiniMessageTags) {
            return Placeholder.unparsed(tagName, message);
        }

        if (!shouldParsePlaceholderAPIPlaceholders) {
            return shouldAutoCloseTags
                ? Placeholder.component(tagName, MessageUtils.parseToMiniMessage(message))
                : TagResolver.resolver(tagName, Tag.inserting(MessageUtils.parseToMiniMessage(message)));
        }

        if (isRelationalTag && recipient.isEmpty()) {
            return TagResolver.empty();
        }

        final boolean recipientIsChatUser = recipient.isPresent() && recipient.get() instanceof ChatUser;

        final TagResolver papiTag = isRelationalTag && recipientIsChatUser
            ? TagResolver.resolver(
                PapiTagUtils.createPlaceholderAPITag(sender.get().player()),
                PapiTagUtils.createRelPlaceholderAPITag(sender.get().player(), ((ChatUser) recipient.get()).player()),
                PapiTagUtils.createRecipientTag(((ChatUser) recipient.get()).player())
            )
            : PapiTagUtils.createPlaceholderAPITag(sender.get().player());

        return shouldAutoCloseTags
            ? Placeholder.component(tagName, MessageUtils.parseToMiniMessage(message, papiTag))
            : TagResolver.resolver(tagName, Tag.inserting(MessageUtils.parseToMiniMessage(message, papiTag)));
    }

    public @NotNull String name() {
        return tagName;
    }

    public boolean requiresRecipient() {
        return isRelationalTag;
    }

    public boolean parseMini() {
        return shouldParseMiniMessageTags;
    }

    public boolean parsePapi() {
        return shouldParsePlaceholderAPIPlaceholders;
    }

    public boolean closing() {
        return shouldAutoCloseTags;
    }

    public @NotNull String message() {
        return message;
    }
}
