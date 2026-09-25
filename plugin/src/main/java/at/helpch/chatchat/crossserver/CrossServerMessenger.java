package at.helpch.chatchat.crossserver;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.event.CrossServerPMSendEvent;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.channel.ChatChannel;
import at.helpch.chatchat.command.IgnoreCommand;
import at.helpch.chatchat.locale.LocaleMessage;
import at.helpch.chatchat.user.ChatUserImpl;
import at.helpch.chatchat.user.ConsoleUser;
import at.helpch.chatchat.util.ChannelUtils;
import at.helpch.chatchat.util.FormatUtils;
import at.helpch.chatchat.util.MentionProtection;
import at.helpch.chatchat.util.MentionUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/** BungeeCord/Velocity bridge for public chat and acknowledged private messages. */
public final class CrossServerMessenger implements PluginMessageListener {

    private static final String PROXY_CHANNEL = "BungeeCord";
    private static final int LOOKUP_TIMEOUT_TICKS = 60;
    private static final int DELIVERY_TIMEOUT_TICKS = 60;
    private static final List<String> MENTION_PERMISSIONS = List.of(
        MentionUtils.MENTION_PERSONAL_PERMISSION,
        MentionUtils.MENTION_CHANNEL_PERMISSION,
        MentionUtils.MENTION_PERSONAL_BLOCK_OVERRIDE_PERMISSION,
        MentionUtils.MENTION_CHANNEL_BLOCK_OVERRIDE_PERMISSION
    );

    private final ChatChatPlugin plugin;
    private final UUID serverId = UUID.randomUUID();
    private final Map<UUID, PendingLookup> lookups = new HashMap<>();
    private final Map<UUID, PendingDelivery> deliveries = new HashMap<>();
    private final Map<UUID, RemoteReply> replies = new HashMap<>();
    private final Map<UUID, Boolean> received = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<UUID, Boolean> eldest) {
            return size() > 512;
        }
    };

    public CrossServerMessenger(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, PROXY_CHANNEL);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, PROXY_CHANNEL, this);
    }

    public void disable() {
        plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, PROXY_CHANNEL);
        plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, PROXY_CHANNEL, this);
        lookups.clear();
        deliveries.clear();
        replies.clear();
    }

    public void sendPublic(@NotNull final Player sender, @NotNull final Channel channel,
                           @NotNull final Component formatted, @NotNull final String messageMarker,
                           final boolean bypassIgnore) {
        final var packet = packet("public", UUID.randomUUID());
        packet.addProperty("channel", channel.name());
        CrossServerPacket.uuid(packet, "sender", sender.getUniqueId());
        packet.addProperty("bypassIgnore", bypassIgnore);
        packet.addProperty("component", GsonComponentSerializer.gson().serialize(formatted));
        packet.addProperty("messageMarker", messageMarker);
        packet.addProperty("protectionMarker", MentionProtection.transportMarker());
        final var permissions = new JsonArray();
        MENTION_PERMISSIONS.stream().filter(sender::hasPermission).forEach(permissions::add);
        packet.add("mentionPermissions", permissions);
        runSync(() -> forward(sender, "ALL", packet));
    }

    public void sendPrivate(@NotNull final ChatUser sender, @NotNull final String recipientName,
                            @NotNull final String message, final boolean reply) {
        final var player = sender.player().orElse(null);
        if (player == null) {
            return;
        }
        if (!plugin.configManager().settings().privateMessagesSettings().enabled() ||
            !plugin.configManager().settings().privateMessagesSettings().crossServer()) {
            plugin.sendConfiguredMessage(sender, LocaleMessage.USER_OFFLINE);
            return;
        }
        if (!sender.privateMessages()) {
            plugin.sendConfiguredMessage(sender, LocaleMessage.REPLIES_DISABLED);
            return;
        }
        if (recipientName.equalsIgnoreCase(player.getName())) {
            plugin.sendConfiguredMessage(sender, LocaleMessage.CANT_MESSAGE_YOURSELF);
            return;
        }
        if (message.isBlank()) {
            plugin.sendConfiguredMessage(sender, LocaleMessage.EMPTY_MESSAGE);
            return;
        }

        final var id = UUID.randomUUID();
        lookups.put(id, new PendingLookup(sender.uuid(), recipientName, message, reply));
        final var packet = packet("lookup", id);
        CrossServerPacket.uuid(packet, "sender", sender.uuid());
        packet.addProperty("senderName", player.getName());
        packet.addProperty("recipientName", recipientName);
        packet.addProperty("reply", reply);
        if (!forward(player, recipientName, packet)) {
            lookups.remove(id);
            plugin.sendConfiguredMessage(sender, LocaleMessage.CROSS_SERVER_UNAVAILABLE);
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (lookups.remove(id) != null) {
                sender.player().ifPresent(online -> plugin.sendConfiguredMessage(sender, LocaleMessage.USER_OFFLINE));
            }
        }, LOOKUP_TIMEOUT_TICKS);
    }

    public @NotNull Optional<RemoteReply> replyTarget(@NotNull final UUID owner) {
        final var reply = replies.get(owner);
        if (reply == null || reply.expiresAt() < System.currentTimeMillis()) {
            replies.remove(owner);
            return Optional.empty();
        }
        return Optional.of(reply);
    }

    public void clearReplyTarget(@NotNull final UUID owner) {
        replies.remove(owner);
    }

    @Override
    public void onPluginMessageReceived(@NotNull final String channel, @NotNull final Player carrier,
                                        final byte @NotNull [] message) {
        if (!PROXY_CHANNEL.equals(channel)) {
            return;
        }
        final JsonObject packet;
        try (final var input = new DataInputStream(new ByteArrayInputStream(message))) {
            if (!CrossServerPacket.SUB_CHANNEL.equals(input.readUTF())) {
                return;
            }
            final var length = input.readUnsignedShort();
            if (length != input.available()) {
                return;
            }
            packet = CrossServerPacket.decode(input.readNBytes(length));
        } catch (final IOException exception) {
            plugin.getLogger().log(Level.WARNING, "Invalid cross-server packet", exception);
            return;
        }
        runSync(() -> handle(packet));
    }

    private void handle(@NotNull final JsonObject packet) {
        try {
            if (serverId.equals(CrossServerPacket.uuid(packet, "origin"))) {
                return;
            }
            final var id = CrossServerPacket.uuid(packet, "id");
            switch (CrossServerPacket.string(packet, "type")) {
                case "public" -> receivePublic(id, packet);
                case "lookup" -> receiveLookup(id, packet);
                case "lookup-reply" -> receiveLookupReply(id, packet);
                case "private" -> receivePrivate(id, packet);
                case "private-reply" -> receivePrivateReply(id, packet);
                default -> plugin.getLogger().warning("Unknown cross-server packet type");
            }
        } catch (final RuntimeException exception) {
            plugin.getLogger().log(Level.WARNING, "Invalid cross-server packet", exception);
        }
    }

    private void receivePublic(@NotNull final UUID id, @NotNull final JsonObject packet) {
        if (received.putIfAbsent(id, true) != null) {
            return;
        }
        final var channel = plugin.configManager().channels().channels().get(CrossServerPacket.string(packet, "channel"));
        if (channel == null || !channel.crossServer() || channel.radius() != -1) {
            return;
        }
        final var senderId = CrossServerPacket.uuid(packet, "sender");
        final var bypassIgnore = packet.get("bypassIgnore").getAsBoolean();
        final var component = MentionProtection.rebase(
            GsonComponentSerializer.gson().deserialize(CrossServerPacket.string(packet, "component")),
            CrossServerPacket.string(packet, "protectionMarker"));
        final var messageMarker = CrossServerPacket.string(packet, "messageMarker");
        final Set<String> permissions = new HashSet<>();
        for (final var permission : packet.getAsJsonArray("mentionPermissions")) {
            final var node = permission.getAsString();
            if (MENTION_PERMISSIONS.contains(node)) {
                permissions.add(node);
            }
        }
        for (final var user : plugin.usersHolder().users()) {
            if (!(user instanceof ChatUser) || !user.chatEnabled() || ((ChatUser) user).player().isEmpty()) {
                continue;
            }
            if (!channel.equals(ChatChannel.defaultChannel()) &&
                !user.hasPermission(ChannelUtils.SEE_CHANNEL_PERMISSION + channel.name())) {
                continue;
            }
            if (plugin.separationManager().isSeparated(senderId, user.uuid()) ||
                (user.ignoredUsers().contains(senderId) && !bypassIgnore)) {
                continue;
            }
            final var remoteSender = new RemoteMentionSender(senderId, permissions,
                () -> !hiddenFromLocalViewer((ChatUser) user));
            remoteSender.channel(channel);
            final var result = RemoteMessageTemplate.process(component, messageMarker,
                body -> plugin.mentionsManager().processMentions(false, remoteSender, user,
                    channel, body, true));
            user.sendMessage(MentionProtection.restore(result.component()));
            if (result.playSound()) {
                user.playSound(plugin.configManager().settings().mentions().sound());
            }
        }
        ConsoleUser.INSTANCE.sendMessage(MentionProtection.restore(
            RemoteMessageTemplate.strip(component, messageMarker)));
    }

    private void receiveLookup(@NotNull final UUID id, @NotNull final JsonObject packet) {
        if (!plugin.configManager().settings().privateMessagesSettings().enabled() ||
            !plugin.configManager().settings().privateMessagesSettings().crossServer()) {
            return;
        }
        final var targetName = CrossServerPacket.string(packet, "recipientName");
        final var target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            return;
        }
        final var recipient = (ChatUser) plugin.usersHolder().getUser(target);
        if (!packet.get("reply").getAsBoolean() && hiddenFromLocalViewer(recipient)) {
            return;
        }
        final var response = packet("lookup-reply", id);
        CrossServerPacket.uuid(response, "recipient", recipient.uuid());
        response.addProperty("recipientName", target.getName());
        response.addProperty("status", recipient.privateMessages() ? "ok" : "disabled");
        forward(target, CrossServerPacket.string(packet, "senderName"), response);
    }

    private void receiveLookupReply(@NotNull final UUID id, @NotNull final JsonObject packet) {
        final var pending = lookups.remove(id);
        if (pending == null) {
            return;
        }
        final var sender = localUser(pending.senderId());
        if (sender == null) {
            return;
        }
        if (!CrossServerPacket.string(packet, "status").equals("ok")) {
            plugin.sendConfiguredMessage(sender, LocaleMessage.TARGET_REPLIES_DISABLED);
            return;
        }
        final var recipientId = CrossServerPacket.uuid(packet, "recipient");
        final var recipientName = CrossServerPacket.string(packet, "recipientName");
        if (!recipientName.equalsIgnoreCase(pending.recipientName())) {
            return;
        }
        if (plugin.separationManager().isSeparated(sender.uuid(), recipientId)) {
            plugin.sendConfiguredMessage(sender, LocaleMessage.CANT_MESSAGE_GENERAL);
            return;
        }
        if (sender.ignoredUsers().contains(recipientId)) {
            plugin.sendConfiguredMessage(sender, LocaleMessage.CANT_MESSAGE_IGNORED_PLAYER);
            return;
        }
        final var remoteRecipient = new ChatUserImpl(recipientId);
        final var rulesResult = plugin.ruleManager().isAllowedPrivateChat(sender, remoteRecipient, pending.message());
        if (rulesResult.isPresent()) {
            sender.sendMessage(rulesResult.get());
            return;
        }
        final var player = sender.player().orElse(null);
        if (player == null) {
            return;
        }
        final var formats = plugin.configManager().settings().privateMessagesSettings().formats();
        final var event = new CrossServerPMSendEvent(sender, recipientId, recipientName,
            formats.senderFormat(), formats.recipientFormat(), formats.socialSpyFormat(),
            Component.text(pending.message()), pending.reply());
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        final var senderView = FormatUtils.parseRemotePrivateFormat(event.senderFormat(), player, recipientName, event.message());
        final var recipientView = FormatUtils.parseRemotePrivateFormat(event.recipientFormat(), player, recipientName, event.message());
        final var spyView = FormatUtils.parseRemotePrivateFormat(event.socialSpyFormat(), player, recipientName, event.message());
        deliveries.put(id, new PendingDelivery(sender.uuid(), recipientId, recipientName, senderView, spyView));
        final var request = packet("private", id);
        CrossServerPacket.uuid(request, "sender", sender.uuid());
        CrossServerPacket.uuid(request, "recipient", recipientId);
        request.addProperty("senderName", player.getName());
        request.addProperty("recipientName", recipientName);
        request.addProperty("bypassIgnore", sender.hasPermission(IgnoreCommand.IGNORE_BYPASS_PERMISSION));
        request.addProperty("component", GsonComponentSerializer.gson().serialize(recipientView));
        request.addProperty("spyComponent", GsonComponentSerializer.gson().serialize(spyView));
        if (!forward(player, recipientName, request)) {
            deliveries.remove(id);
            plugin.sendConfiguredMessage(sender, LocaleMessage.CROSS_SERVER_UNAVAILABLE);
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (deliveries.remove(id) != null) {
                sender.player().ifPresent(online -> plugin.sendConfiguredMessage(sender, LocaleMessage.CROSS_SERVER_UNAVAILABLE));
            }
        }, DELIVERY_TIMEOUT_TICKS);
    }

    private void receivePrivate(@NotNull final UUID id, @NotNull final JsonObject packet) {
        if (received.putIfAbsent(id, true) != null) {
            return;
        }
        final var target = Bukkit.getPlayerExact(CrossServerPacket.string(packet, "recipientName"));
        if (target == null || !target.getUniqueId().equals(CrossServerPacket.uuid(packet, "recipient"))) {
            return;
        }
        final var recipient = (ChatUser) plugin.usersHolder().getUser(target);
        final var senderId = CrossServerPacket.uuid(packet, "sender");
        var status = "ok";
        if (!plugin.configManager().settings().privateMessagesSettings().enabled() ||
            !plugin.configManager().settings().privateMessagesSettings().crossServer() ||
            !recipient.privateMessages()) {
            status = LocaleMessage.TARGET_REPLIES_DISABLED.name();
        } else if (plugin.separationManager().isSeparated(senderId, recipient.uuid()) ||
            (recipient.ignoredUsers().contains(senderId) && !packet.get("bypassIgnore").getAsBoolean())) {
            status = LocaleMessage.CANT_MESSAGE_GENERAL.name();
        }
        if (status.equals("ok")) {
            recipient.sendMessage(GsonComponentSerializer.gson().deserialize(CrossServerPacket.string(packet, "component")));
            final var spy = GsonComponentSerializer.gson().deserialize(CrossServerPacket.string(packet, "spyComponent"));
            sendToSpies(spy, senderId, recipient.uuid());
            if (plugin.configManager().settings().mentions().privateMessage()) {
                recipient.playSound(plugin.configManager().settings().mentions().sound());
            }
            setReplyTarget(recipient, senderId, CrossServerPacket.string(packet, "senderName"));
        }
        final var response = packet("private-reply", id);
        CrossServerPacket.uuid(response, "recipient", recipient.uuid());
        response.addProperty("status", status);
        forward(target, CrossServerPacket.string(packet, "senderName"), response);
    }

    private void receivePrivateReply(@NotNull final UUID id, @NotNull final JsonObject packet) {
        final var pending = deliveries.remove(id);
        if (pending == null || !pending.recipientId().equals(CrossServerPacket.uuid(packet, "recipient"))) {
            return;
        }
        final var sender = localUser(pending.senderId());
        if (sender == null) {
            return;
        }
        final var status = CrossServerPacket.string(packet, "status");
        if (!status.equals("ok")) {
            plugin.sendConfiguredMessage(sender, LocaleMessage.valueOf(status));
            return;
        }
        sender.sendMessage(pending.senderView());
        sendToSpies(pending.spyView(), sender.uuid(), pending.recipientId());
        setReplyTarget(sender, pending.recipientId(), pending.recipientName());
    }

    private void sendToSpies(@NotNull final Component component, @NotNull final UUID sender,
                             @NotNull final UUID recipient) {
        plugin.usersHolder().users().stream()
            .filter(user -> !user.uuid().equals(sender) && !user.uuid().equals(recipient))
            .filter(user -> !(user instanceof ChatUser) || ((ChatUser) user).socialSpy())
            .forEach(user -> user.sendMessage(component));
    }

    private void setReplyTarget(@NotNull final ChatUser owner, @NotNull final UUID remoteId,
                                @NotNull final String remoteName) {
        final var duration = ChatChatPlugin.cacheDuration();
        final var expiresAt = duration < 0 ? Long.MAX_VALUE :
            System.currentTimeMillis() + duration * 1_000;
        replies.put(owner.uuid(), new RemoteReply(remoteId, remoteName, expiresAt));
        owner.lastMessagedUser(null);
    }

    private ChatUser localUser(@NotNull final UUID id) {
        final var player = Bukkit.getPlayer(id);
        return player == null ? null : (ChatUser) plugin.usersHolder().getUser(player);
    }

    private boolean hiddenFromLocalViewer(@NotNull final ChatUser target) {
        if (target.player().map(player -> player.hasMetadata("vanished")).orElse(false)) {
            return true;
        }
        return plugin.usersHolder().users().stream()
            .filter(ChatUser.class::isInstance)
            .map(ChatUser.class::cast)
            .filter(viewer -> !viewer.uuid().equals(target.uuid()))
            .anyMatch(viewer -> !viewer.canSee(target));
    }

    private @NotNull JsonObject packet(@NotNull final String type, @NotNull final UUID id) {
        final var data = new JsonObject();
        data.addProperty("type", type);
        CrossServerPacket.uuid(data, "id", id);
        CrossServerPacket.uuid(data, "origin", serverId);
        return data;
    }

    private boolean forward(@NotNull final Player carrier, @NotNull final String destination,
                            @NotNull final JsonObject packet) {
        if (!carrier.isOnline()) {
            return false;
        }
        try {
            final var payload = CrossServerPacket.encode(packet);
            final var bytes = new java.io.ByteArrayOutputStream();
            try (final var output = new java.io.DataOutputStream(bytes)) {
                output.writeUTF(destination.equals("ALL") ? "Forward" : "ForwardToPlayer");
                output.writeUTF(destination);
                output.writeUTF(CrossServerPacket.SUB_CHANNEL);
                output.writeShort(payload.length);
                output.write(payload);
            }
            if (bytes.size() > 32_000) {
                return false;
            }
            carrier.sendPluginMessage(plugin, PROXY_CHANNEL, bytes.toByteArray());
            return true;
        } catch (final IOException | IllegalArgumentException exception) {
            plugin.getLogger().log(Level.WARNING, "Could not forward cross-server message", exception);
            return false;
        }
    }

    private void runSync(@NotNull final Runnable action) {
        if (Bukkit.isPrimaryThread()) {
            action.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, action);
        }
    }

    private record PendingLookup(UUID senderId, String recipientName, String message, boolean reply) {
    }

    private record PendingDelivery(UUID senderId, UUID recipientId, String recipientName,
                                   Component senderView, Component spyView) {
    }

    public record RemoteReply(UUID uuid, String name, long expiresAt) {
    }
}
