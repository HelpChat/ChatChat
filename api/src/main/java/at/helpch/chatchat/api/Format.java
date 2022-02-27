package at.helpch.chatchat.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Format {

    @NotNull
    String getId();

    int getPriority();

    @NotNull
    List<Segment> getSegments();

    interface Segment {

        @NotNull
        Component getText();

        @NotNull
        List<Component> getTooltip();

        @Nullable
        ClickEvent.Action getClickAction();

        @Nullable
        String getClickActionValue();

    }

}
