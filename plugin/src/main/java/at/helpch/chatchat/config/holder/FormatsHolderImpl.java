package at.helpch.chatchat.config.holder;

import at.helpch.chatchat.api.format.PriorityFormat;
import at.helpch.chatchat.api.holder.FormatsHolder;
import at.helpch.chatchat.config.DefaultConfigObjects;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;

// configurate requires non-final fields
@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public class FormatsHolderImpl implements FormatsHolder {

    private Map<String, PriorityFormat> formats = Map.of(
        "default-channel", DefaultConfigObjects.createDefaultChannelFormat());

    public @NotNull Map<String, PriorityFormat> formats() {
        return Map.copyOf(formats);
    }
}
