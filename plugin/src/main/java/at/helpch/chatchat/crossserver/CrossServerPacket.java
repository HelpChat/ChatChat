package at.helpch.chatchat.crossserver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** A bounded, versioned payload inside the proxy's Forward packet. */
final class CrossServerPacket {

    static final String SUB_CHANNEL = "ChatChat";
    private static final int VERSION = 2;
    private static final int MAX_BYTES = 30_000;

    private CrossServerPacket() {
    }

    static byte @NotNull [] encode(@NotNull final JsonObject data) throws IOException {
        final var payload = data.toString().getBytes(StandardCharsets.UTF_8);
        if (payload.length > MAX_BYTES) {
            throw new IOException("Cross-server message exceeds " + MAX_BYTES + " bytes");
        }
        final var bytes = new ByteArrayOutputStream();
        try (final var output = new DataOutputStream(bytes)) {
            output.writeByte(VERSION);
            output.writeInt(payload.length);
            output.write(payload);
        }
        return bytes.toByteArray();
    }

    static @NotNull JsonObject decode(final byte @NotNull [] bytes) throws IOException {
        if (bytes.length < 5 || bytes.length > MAX_BYTES + 5) {
            throw new IOException("Invalid cross-server message length");
        }
        try (final var input = new DataInputStream(new ByteArrayInputStream(bytes))) {
            if (input.readUnsignedByte() != VERSION) {
                throw new IOException("Unsupported cross-server message version");
            }
            final var length = input.readInt();
            if (length < 0 || length > MAX_BYTES || length != input.available()) {
                throw new IOException("Invalid cross-server payload length");
            }
            final var data = JsonParser.parseString(new String(input.readNBytes(length), StandardCharsets.UTF_8));
            if (!data.isJsonObject()) {
                throw new IOException("Cross-server payload is not an object");
            }
            return data.getAsJsonObject();
        } catch (final RuntimeException exception) {
            throw new IOException("Invalid cross-server payload", exception);
        }
    }

    static void uuid(@NotNull final JsonObject data, @NotNull final String field, @NotNull final UUID value) {
        data.addProperty(field, value.toString());
    }

    static @NotNull UUID uuid(@NotNull final JsonObject data, @NotNull final String field) {
        return UUID.fromString(data.get(field).getAsString());
    }

    static @NotNull String string(@NotNull final JsonObject data, @NotNull final String field) {
        return data.get(field).getAsString();
    }
}
