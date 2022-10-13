package at.helpch.chatchat.config.holder;

import at.helpch.chatchat.placeholder.MiniPlaceholderImpl;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Set;

// configurate requires non-final fields
@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public class MiniPlaceholdersHolder {

    private Set<MiniPlaceholderImpl> placeholders = ImmutableSet.of();

    public @NotNull Set<MiniPlaceholderImpl> placeholders() {
        return placeholders;
    }
}
