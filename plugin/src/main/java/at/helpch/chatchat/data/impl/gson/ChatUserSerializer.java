package at.helpch.chatchat.data.impl.gson;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.user.ChatUserImpl;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.UUID;

public class ChatUserSerializer implements JsonSerializer<ChatUser>, JsonDeserializer<ChatUser> {

    private final ChatChatPlugin plugin;

    public ChatUserSerializer(final @NotNull ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ChatUser deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null) {
            throw new JsonParseException("Invalid JSON object was found!");
        }

        final JsonObject jsonObject;
        try {
            jsonObject = json.getAsJsonObject();
        } catch (final IllegalAccessError exception) {
            throw new JsonParseException("Invalid JSON object was found!");
        }

        if (jsonObject == null) {
            throw new JsonParseException("Invalid JSON object was found!");
        }

        if (!jsonObject.has("uuid")) {
            throw new JsonParseException("Expected JSON object to have property 'uuid'");
        }
        final var uuidAsString = jsonObject.get("uuid").getAsString();
        final UUID uuid;
        try {
            uuid = UUID.fromString(uuidAsString);
        } catch (final IllegalArgumentException exception) {
            throw new JsonParseException("The UUID is invalid!", exception);
        }

        if (!jsonObject.has("channel")) {
            throw new JsonParseException("Expected JSON object to have property 'channel'");
        }
        final var channelName = jsonObject.get("channel").getAsString();
        final var contains = plugin.configManager().channels().channels().containsKey(channelName);

        final var channel = contains
            ? plugin.configManager().channels().channels().get(channelName)
            : ChatChannel.defaultChannel();

        if (channel == null) {
            throw new JsonParseException("Channel '" + channelName + "' not found!");
        }

        if (!jsonObject.has("private-messages")) {
            throw new JsonParseException("Expected JSON object to have property 'private-messages'");
        }
        final var privateMessages = jsonObject.get("private-messages").getAsBoolean();

        if (!jsonObject.has("personal-mentions")) {
            throw new JsonParseException("Expected JSON object to have property 'personal-mentions'");
        }
        final var personalMentions = jsonObject.get("personal-mentions").getAsBoolean();

        if (!jsonObject.has("channel-mentions")) {
            throw new JsonParseException("Expected JSON object to have property 'channel-mentions'");
        }
        final var channelMentions = jsonObject.get("channel-mentions").getAsBoolean();

        if (!jsonObject.has("social-spy")) {
            throw new JsonParseException("Expected JSON object to have property 'social-spy'");
        }
        final var socialSpy = jsonObject.get("social-spy").getAsBoolean();

        if (!jsonObject.has("chat-enabled")) {
            throw new JsonParseException("Expected JSON object to have property 'chat-enabled'");
        }
        final var chatEnabled = jsonObject.get("chat-enabled").getAsBoolean();

        if (!jsonObject.has("ignored-users")) {
            throw new JsonParseException("Expected JSON object to have property 'ignored-users'");
        }
        final var ignoredUsers = new HashSet<UUID>();
        for (final JsonElement element : jsonObject.get("ignored-users").getAsJsonArray()) {
            final var uuidString = element.getAsString();
            final UUID uuidFromString;
            try {
                uuidFromString = UUID.fromString(uuidString);
            } catch (final IllegalArgumentException exception) {
                throw new JsonParseException("Ignored User UUID (" + uuidString + ") is invalid!", exception);
            }
            ignoredUsers.add(uuidFromString);
        }

        final ChatUser user = new ChatUserImpl(uuid);
        if (channel.isUsableBy(user))
            user.channel(channel);
        else {
            user.channel(ChatChannel.defaultChannel());
        }

        user.privateMessages(privateMessages);
        user.personalMentions(personalMentions);
        user.channelMentions(channelMentions);
        user.socialSpy(socialSpy);
        user.chatState(chatEnabled);
        user.ignoredUsers(ignoredUsers);

        return user;
    }

    @Override
    public JsonElement serialize(ChatUser src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;

        final var jsonObject = new JsonObject();

        jsonObject.addProperty("uuid", src.uuid().toString());
        jsonObject.addProperty("channel", src.channel().name());
        jsonObject.addProperty("private-messages", src.privateMessages());
        jsonObject.addProperty("personal-mentions", src.personalMentions());
        jsonObject.addProperty("channel-mentions", src.channelMentions());
        jsonObject.addProperty("social-spy", src.socialSpy());
        jsonObject.addProperty("chat-enabled", src.chatEnabled());

        final var ignoredUsers = new JsonArray();
        for (UUID uuid : src.ignoredUsers()) {
            ignoredUsers.add(uuid.toString());
        }

        jsonObject.add("ignored-users", ignoredUsers);

        return jsonObject;
    }

}
