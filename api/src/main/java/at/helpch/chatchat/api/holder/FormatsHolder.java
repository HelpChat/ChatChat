package at.helpch.chatchat.api.holder;

import at.helpch.chatchat.api.format.PriorityFormat;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * This is used to store a list of @link PriorityFormat}s.
 */
public interface FormatsHolder {

    /**
     * A map of all formats. The map should be of type Name to Format.
     *
     * @return The map of formats.
     */
    @NotNull Map<String, PriorityFormat> formats();
}
