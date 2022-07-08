package at.helpch.chatchat.hooks.towny;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.ResidentList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class TownyTownChannel extends AbstractTownyChannel {

    public TownyTownChannel(@NotNull final String name,
                            @NotNull final String messagePrefix,
                            @NotNull final List<String> toggleCommands,
                            @NotNull final String channelPrefix,
                            final int radius) {
        super(name, messagePrefix, toggleCommands, channelPrefix, radius);
    }

    @Override
    protected @Nullable ResidentList residentList(@NotNull final Resident resident) {
        return resident.getTownOrNull();
    }
}
