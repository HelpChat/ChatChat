package at.helpch.chatchat.data.impl.gson;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.user.ChatUserImpl;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

public final class ChatUserAdapter extends TypeAdapter<ChatUser> {

    private final ChatChatPlugin plugin;

    public ChatUserAdapter(ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void write(JsonWriter out, ChatUser value) throws IOException {
        out.beginObject();

        out.name("uuid");
        out.value(value.uuid().toString());
        out.name("channel");
        out.value(value.channel().name());
        out.name("private-messages");
        out.value(value.privateMessages());
        out.name("personal-mentions");
        out.value(value.personalMentions());
        out.name("channel-mentions");
        out.value(value.channelMentions());
        out.name("social-spy");
        out.value(value.socialSpy());
        out.name("ignored-users");
        out.beginArray();
        for (UUID uuid : value.ignoredUsers()) {
            out.value(uuid.toString());
        }
        out.endArray();

        out.endObject();
    }

    @Override
    public ChatUser read(JsonReader in) throws IOException {
        in.beginObject();

        if (!in.hasNext()) {
            in.close();
            throw new JsonParseException("Expected JSON object to have property 'uuid'");
        }

        in.nextName();
        final var uuid = in.nextString();

        if (!in.hasNext()) {
            in.close();
            throw new JsonParseException("Expected JSON object to have property 'channel'");
        }

        in.nextName();
        final var channelName = in.nextString();
        final var contains = plugin.configManager().channels().channels().containsKey(channelName);

        final var channel = contains
            ? plugin.configManager().channels().channels().get(channelName)
            : plugin.configManager().channels().channels().get(plugin.configManager().channels().defaultChannel());

        if (channel == null) {
            in.close();
            throw new JsonParseException("Channel '" + channelName + "' not found!");
        }

        if (!in.hasNext()) {
            in.close();
            throw new JsonParseException("Expected JSON object to have property 'private-messages'");
        }

        in.nextName();
        final var privateMessages = in.nextBoolean();

        if (!in.hasNext()) {
            in.close();
            throw new JsonParseException("Expected JSON object to have property 'personal-mentions'");
        }

        in.nextName();
        final var personalMentions = in.nextBoolean();

        if (!in.hasNext()) {
            in.close();
            throw new JsonParseException("Expected JSON object to have property 'channel-mentions'");
        }

        in.nextName();
        final var channelMentions = in.nextBoolean();

        if (!in.hasNext()) {
            in.close();
            throw new JsonParseException("Expected JSON object to have property 'social-spy'");
        }

        in.nextName();
        final var socialSpy = in.nextBoolean();

        if (!in.hasNext()) {
            in.close();
            throw new JsonParseException("Expected JSON object to have property 'ignored-users'");
        }

        in.nextName();
        in.beginArray();
        final var ignoredUsers = new HashSet<UUID>();
        while (in.hasNext()) {
            final var uuidString = in.nextString();
            final UUID uuidFromString;
            try {
                uuidFromString = UUID.fromString(uuidString);
            } catch (final IllegalArgumentException exception) {
                throw new JsonParseException("Ignored User UUID (" + uuidString + ") is invalid!", exception);
            }
            ignoredUsers.add(uuidFromString);
        }
        in.endArray();

        in.endObject();

        final ChatUser user;
        try {
            user = new ChatUserImpl(UUID.fromString(uuid));
        } catch (final IllegalArgumentException exception) {
            throw new JsonParseException("The UUID is invalid!", exception);
        }
        user.channel(channel);
        user.privateMessages(privateMessages);
        user.personalMentions(personalMentions);
        user.channelMentions(channelMentions);
        user.socialSpy(socialSpy);
        user.ignoredUsers(ignoredUsers);

        return user;
    }

}
