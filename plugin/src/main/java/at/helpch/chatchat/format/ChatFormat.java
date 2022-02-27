package at.helpch.chatchat.format;

import at.helpch.chatchat.api.Format;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ChatFormat implements Format {

    private String id = "";
    private int priority = Integer.MAX_VALUE;
    private List<Segment> segments = Collections.emptyList();

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public @NotNull List<Segment> getSegments() {
        return segments;
    }

}
