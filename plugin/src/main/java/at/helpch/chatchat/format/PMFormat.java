package at.helpch.chatchat.format;

import at.helpch.chatchat.api.Format;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;

@ConfigSerializable
public class PMFormat implements Format {

    private List<String> parts = Collections.emptyList();

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public @NotNull List<String> getParts() {
        return parts;
    }

    public void setParts(@NotNull final List<String> parts) {
        this.parts = parts;
    }

    @Override
    public String toString() {
        return "PMFormat{" +
                "priority=" + getPriority() +
                ", parts=" + parts +
                '}';
    }
}
