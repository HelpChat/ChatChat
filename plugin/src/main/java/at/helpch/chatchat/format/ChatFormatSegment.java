package at.helpch.chatchat.format;

import at.helpch.chatchat.api.Format;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ChatFormatSegment implements Format.Segment {

    private Component text = Component.empty();
    private List<Component> tooltip = Collections.emptyList();
    private ClickEvent.Action clickAction;
    private String clickActionValue = "";

    @Override
    public @NotNull Component getText() {
        return text;
    }

    @Override
    public @NotNull List<Component> getTooltip() {
        return tooltip;
    }

    @Override
    public @Nullable ClickEvent.Action getClickAction() {
        return clickAction;
    }

    @Override
    public @Nullable String getClickActionValue() {
        return clickActionValue;
    }

}
