package at.helpch.chatchat.config.holder;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public class AddonsHolder {

    // DeluxeChat features
    private boolean DELUXECHAT_INVERSE_PRIORITIES = false;
    private boolean DELUXECHAT_UNICODE_PERMISSION_PUBLIC_CHAT = true;
    private boolean DELUXECHAT_UNICODE_PERMISSION_PRIVATE_CHAT = true;

    // Towny features
    private boolean TOWNY_CHANNELS = false;

    // DiscordSRV features
    private boolean DISCORDSRV_CHANNELS_BRIDGING = false;

    // Essentials features
    private boolean ESSENTIALS_VANISH = false;

    // SuperVanish features
    private boolean SUPERVANISH_VANISH = false;

    // GriefPrevention features
    private boolean GRIEFPREVENTION_SOFT_MUTE = true;

    public boolean deluxeChatInversePriorities() {
        return DELUXECHAT_INVERSE_PRIORITIES;
    }

    public void deluxeChatInversePriorities(final boolean value) {
        DELUXECHAT_INVERSE_PRIORITIES = value;
    }

    public boolean deluxeChatUnicodePermissionPublicChat() {
        return DELUXECHAT_UNICODE_PERMISSION_PUBLIC_CHAT;
    }

    public void deluxeChatUnicodePermissionPublicChat(final boolean value) {
        DELUXECHAT_UNICODE_PERMISSION_PUBLIC_CHAT = value;
    }

    public boolean deluxeChatUnicodePermissionPrivateChat() {
        return DELUXECHAT_UNICODE_PERMISSION_PRIVATE_CHAT;
    }

    public void deluxeChatUnicodePermissionPrivateChat(final boolean value) {
        DELUXECHAT_UNICODE_PERMISSION_PRIVATE_CHAT = value;
    }

    public boolean townyChannels() {
        return TOWNY_CHANNELS;
    }

    public void townyChannels(final boolean value) {
        TOWNY_CHANNELS = value;
    }

    public boolean discordSrvChannelsBridging() {
        return DISCORDSRV_CHANNELS_BRIDGING;
    }

    public void discordSrvChannelsBridging(final boolean value) {
        DISCORDSRV_CHANNELS_BRIDGING = value;
    }

    public boolean essentialsVanish() {
        return ESSENTIALS_VANISH;
    }

    public void essentialsVanish(final boolean value) {
        ESSENTIALS_VANISH = value;
    }

    public boolean superVanishVanish() {
        return SUPERVANISH_VANISH;
    }

    public void superVanishVanish(final boolean value) {
        SUPERVANISH_VANISH = value;
    }

    public boolean griefPreventionSoftMute() {
        return GRIEFPREVENTION_SOFT_MUTE;
    }

    public void griefPreventionSoftMute(final boolean value) {
        GRIEFPREVENTION_SOFT_MUTE = value;
    }
}
