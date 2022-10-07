package at.helpch.chatchat.api.holder;

import at.helpch.chatchat.api.format.Format;
import at.helpch.chatchat.api.format.PriorityFormat;
import org.jetbrains.annotations.NotNull;

/**
 * This is used to store the name of the default format, a {@link Format} that is used
 * for console, and a list of {@link PriorityFormat}s.
 */
public interface GlobalFormatsHolder extends FormatsHolder {

    /**
     * Get the name of the default format. The default is a format that everyone has access to and is what will be used
     * if the user does not have access to any other format.
     *
     * @return The name of the default format.
     */
    @NotNull String defaultFormat();

    /**
     * Get the console format. This is the format that is used for console output. Console needs a special format
     * because spigot does not support Components so not all MiniMessage features can be used.
     *
     * @return The console format.
     */
    @NotNull Format consoleFormat();
}
