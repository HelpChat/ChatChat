package at.helpch.chatchat.api.format;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a basic {@link Format} that has a priority. These are usually used for player formats.
 */
public interface PriorityFormat extends Format {

    /**
     * Get the priority of the format.
     *
     * @return the priority of the format.
     */
    int priority();

    /**
     * Set the priority of the format. A thing to note is that formats are immutable in ChatChat so instead of
     * changing the priority, this will create and return a copy of this format with the new priority.
     *
     * @param priority the new priority of the format.
     * @return a copy of this format with the new priority.
     */
    @NotNull PriorityFormat priority(final int priority);

}
