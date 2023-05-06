package at.helpch.chatchat.config.holder;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public class ExtensionsHolder {

    private AddonsHolder addons = new AddonsHolder();

    public @NotNull AddonsHolder addons() {
        return addons;
    }

}
