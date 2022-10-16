package at.helpch.chatchat.api.format;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface Format {

    /**
     * This method is used to get the name of the format.
     *
     * @return the name of the format.
     */
    @NotNull String name();

    /**
     * This method is used to override the name of the format. A thing to note is that formats are immutable in
     * ChatChat so instead of overriding, this will create and return a copy of this format with the new name.
     *
     * @param name the new name of the format.
     * @return a copy of the current format with the new name.
     */
    @NotNull Format name(@NotNull final String name);

    /**
     * This method is used to get the parts of the format. The parts are made out of a part name and a list of strings.
     * ChatChat will just append the parts into one big string.
     *
     * @return the parts of the format.
     */
    @NotNull Map<String, List<String>> parts();

    /**
     * This method is used to override the parts of the format. A thing to note is that formats are immutable in
     * ChatChat so instead of overriding, this will create and return a copy of this format with the new parts.
     *
     * @param parts the new parts of the format.
     * @return a copy of the current format with the new parts.
     */
    @NotNull Format parts(@NotNull final Map<String, List<String>> parts);
}
