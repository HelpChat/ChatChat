package at.helpch.chatchat.api.holder;

import at.helpch.chatchat.api.format.PriorityFormat;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface FormatsHolder {
    @NotNull Map<String, PriorityFormat> formats();
}
