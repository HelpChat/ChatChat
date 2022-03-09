package at.helpch.chatchat.format;

import at.helpch.chatchat.api.Format;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;

@ConfigSerializable
public class ChatFormat implements Format {

    private int priority = Integer.MAX_VALUE;
    private List<String> parts = Collections.emptyList();

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public @NotNull List<String> getParts() {
        return parts;
    }

}
