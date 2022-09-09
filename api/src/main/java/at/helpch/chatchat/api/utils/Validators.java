package at.helpch.chatchat.api.utils;

import at.helpch.chatchat.api.hook.Hook;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public final class Validators {

    private final static Pattern HOOK_NAME_PATTERN = Pattern.compile("(?<name>\\w+)");

    private Validators() {
        throw new AssertionError("Util classes are not to be instantiated!");
    }

    /**
     * Checks if the given string is a valid {@link Hook} name.
     *
     * @param name the name to check
     * @return true if and only if the name is valid, false otherwise
     */
    public static boolean isValidHookName(@NotNull final String name) {
        final var matcher = HOOK_NAME_PATTERN.matcher(name);
        if (!matcher.matches()) {
            return false;
        }

        final var hookName = matcher.group("name");

        return hookName != null;
    }

}
