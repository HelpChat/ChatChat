package at.helpch.chatchat.hooks.towny;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.ChatUser;
import at.helpch.chatchat.api.User;
import at.helpch.chatchat.channel.AbstractChannel;
import at.helpch.chatchat.util.ChannelUtils;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.ResidentList;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractTownyChannel extends AbstractChannel {
    protected AbstractTownyChannel(@NotNull final String name,
                                   @NotNull final String messagePrefix,
                                   @NotNull final String toggleCommand,
                                   @NotNull final String channelPrefix,
                                   final int radius) {
        super(name, messagePrefix, toggleCommand, channelPrefix, radius);
        if (Bukkit.getPluginManager().getPlugin("Towny") == null) {
            throw new RuntimeException("Attempting to use a Towny channel but Towny is not installed.");
        }}

    private Optional<ResidentList> residentList(@NotNull final UUID uuid) {
        return TownyUniverse.getInstance().getResidentOpt(uuid).map(this::residentList);
    }

    @Override
    public boolean isUseableBy(@NotNull final ChatUser user) {
        return super.isUseableBy(user) && residentList(user.uuid()).isPresent();
    }

    protected abstract @Nullable ResidentList residentList(@NotNull final Resident resident);

    private final ChatChatPlugin plugin = ChatChatPlugin.getPlugin(ChatChatPlugin.class);

    @Override
    public Set<User> targets(final @NotNull User source) {
        final var list = residentList(source.uuid());
        if (list.isEmpty()) return Set.of();
        return list.get().getResidents().stream()
                .map(Resident::getUUID)
                .map(plugin.usersHolder()::getUser)
                .filter(target -> ChannelUtils.isTargetWithinRadius(source, target, radius()))
                .collect(Collectors.toSet());
    }
}
