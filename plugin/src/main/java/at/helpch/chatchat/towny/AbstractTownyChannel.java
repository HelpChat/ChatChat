package at.helpch.chatchat.towny;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.channel.AbstractChannel;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.ResidentList;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractTownyChannel extends AbstractChannel {
    protected AbstractTownyChannel(@NotNull final String name,
                                   @NotNull final String messagePrefix,
                                   @NotNull final String toggleCommand,
                                   @NotNull final String channelPrefix) {
        super(name, messagePrefix, toggleCommand, channelPrefix);
        if (Bukkit.getPluginManager().getPlugin("Towny") == null) {
            throw new RuntimeException("Attempting to use a Towny channel but Towny is not installed");
        }}

    private Optional<ResidentList> residentList(@NotNull final UUID uuid) {
        return TownyUniverse.getInstance().getResidentOpt(uuid).map(this::residentList);
    }

    @Override
    public boolean isUseableBy(@NotNull final ChatUser user) {
        // TODO - permission checks
        return residentList(user.uuid()).isPresent();
    }

    protected abstract @Nullable ResidentList residentList(@NotNull final Resident resident);

    @Override
    public void sendMessage(@NotNull final Identity source, @NotNull final Component message, @NotNull final MessageType type) {
        final var list = residentList(source.uuid());
        if (list.isEmpty()) return;
        list.get().getResidents().stream()
                .map(Resident::getUUID)
                .map(ChatChatPlugin.audiences()::player)
                .collect(Audience.toAudience())
                .sendMessage(source, message, type);
    }
}
