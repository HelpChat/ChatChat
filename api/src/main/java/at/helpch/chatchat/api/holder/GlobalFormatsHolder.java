package at.helpch.chatchat.api.holder;

import at.helpch.chatchat.api.Format;
import org.jetbrains.annotations.NotNull;

public interface GlobalFormatsHolder extends FormatsHolder {
    @NotNull String defaultFormat();

    @NotNull Format consoleFormat();
}
