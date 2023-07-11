package at.helpch.chatchat.config.mapper;

import at.helpch.chatchat.config.holder.AddonsHolder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class AddonsMapper implements TypeSerializer<AddonsHolder> {

    private static final Object[] DELUXECHAT_INVERSE_PRIORITIES = new Object[] {"deluxechat", "inverse_priorities"};
    private static final Object[] DELUXECHAT_UNICODE_PERMISSION_PUBLIC_CHAT = new Object[] {"deluxechat", "unicode_permission", "public_chat"};
    private static final Object[] DELUXECHAT_UNICODE_PERMISSION_PRIVATE_CHAT = new Object[] {"deluxechat", "unicode_permission", "private_chat"};

    private static final Object[] TOWNY_CHANNELS = new Object[] {"towny", "channels"};

    private static final Object[] DISCORDSRV_CHANNELS_BRIDGING = new Object[] {"discordsrv", "channels_bridging"};

    private static final Object[] ESSENTIALS_VANISH = new Object[] {"essentials", "vanish"};

    private static final Object[] SUPERVANISH_VANISH = new Object[] {"supervanish", "vanish"};

    private static final Object[] GRIEFPREVENTION_SOFT_MUTE = new Object[] {"griefprevention", "soft_mute"};

    @Override
    public AddonsHolder deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        final AddonsHolder holder = new AddonsHolder();

        holder.deluxeChatInversePriorities(node.node(DELUXECHAT_INVERSE_PRIORITIES).getBoolean(true));
        holder.deluxeChatUnicodePermissionPublicChat(node.node(DELUXECHAT_UNICODE_PERMISSION_PUBLIC_CHAT).getBoolean(true));
        holder.deluxeChatUnicodePermissionPrivateChat(node.node(DELUXECHAT_UNICODE_PERMISSION_PRIVATE_CHAT).getBoolean(true));

        holder.townyChannels(node.node(TOWNY_CHANNELS).getBoolean(false));

        holder.discordSrvChannelsBridging(node.node(DISCORDSRV_CHANNELS_BRIDGING).getBoolean(false));

        holder.essentialsVanish(node.node(ESSENTIALS_VANISH).getBoolean(true));

        holder.superVanishVanish(node.node(SUPERVANISH_VANISH).getBoolean(false));

        holder.griefPreventionSoftMute(node.node(GRIEFPREVENTION_SOFT_MUTE).getBoolean(false));

        return holder;
    }

    @Override
    public void serialize(final Type type, final @Nullable AddonsHolder obj, final ConfigurationNode target) throws SerializationException {
        if (obj == null) {
            return;
        }

        target.node(DELUXECHAT_INVERSE_PRIORITIES).set(obj.deluxeChatInversePriorities());
        target.node(DELUXECHAT_UNICODE_PERMISSION_PUBLIC_CHAT).set(obj.deluxeChatUnicodePermissionPublicChat());
        target.node(DELUXECHAT_UNICODE_PERMISSION_PRIVATE_CHAT).set(obj.deluxeChatUnicodePermissionPrivateChat());

        target.node(TOWNY_CHANNELS).set(obj.townyChannels());

        target.node(DISCORDSRV_CHANNELS_BRIDGING).set(obj.discordSrvChannelsBridging());

        target.node(ESSENTIALS_VANISH).set(obj.essentialsVanish());

        target.node(SUPERVANISH_VANISH).set(obj.superVanishVanish());

        target.node(GRIEFPREVENTION_SOFT_MUTE).set(obj.griefPreventionSoftMute());
    }

}
