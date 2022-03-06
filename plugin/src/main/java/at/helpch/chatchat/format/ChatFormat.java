package at.helpch.chatchat.format;

import at.helpch.chatchat.api.Format;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ChatFormat implements Format {

    private int priority = Integer.MAX_VALUE;
    private List<String> parts = Collections.emptyList();

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public @NotNull List<String> GetParts() {
        return parts;
    }

}
