package at.helpch.chatchat.config.holder;

import at.helpch.chatchat.api.format.PriorityFormat;
import at.helpch.chatchat.api.holder.FormatsHolder;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;

// configurate requires non-final fields
@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public class FormatsHolderImpl implements FormatsHolder {

    public FormatsHolderImpl() {
    }

    public FormatsHolderImpl(Map<String, PriorityFormat> formats) {
        this.formats = formats;
    }

    private Map<String, PriorityFormat> formats = Map.of();

    public @NotNull Map<String, PriorityFormat> formats() {
        return Map.copyOf(formats);
    }
}
