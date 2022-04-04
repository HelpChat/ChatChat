package at.helpch.chatchat.hooks.towny;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.ResidentList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TownyTownChannel extends AbstractTownyChannel {

    public TownyTownChannel(@NotNull final String name,
                            @NotNull final String messagePrefix,
                            @NotNull final String toggleCommand,
                            @NotNull final String channelPrefix) {
        super(name, messagePrefix, toggleCommand, channelPrefix);
    }

    @Override
    protected @Nullable ResidentList residentList(@NotNull final Resident resident) {
        return resident.getTownOrNull();
    }
}
