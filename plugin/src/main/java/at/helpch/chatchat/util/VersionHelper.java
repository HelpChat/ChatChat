package at.helpch.chatchat.util;

import at.helpch.chatchat.api.exception.ChatChatException;
import com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for detecting server version for legacy support :(
 * @author Matt
 */
public final class VersionHelper {

    public static final String NMS_VERSION = getNmsVersion();

    public static final int CURRENT_VERSION = getCurrentVersion();

    public static final int V1_19_0 = 1190;

    /**
     * Gets the current server version
     *
     * @return A protocol like number representing the version, for example 1.16.5 - 1165
     */
    private static int getCurrentVersion() {
        // No need to cache since will only run once
        final Matcher matcher = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?").matcher(Bukkit.getBukkitVersion());

        final StringBuilder stringBuilder = new StringBuilder();
        if (matcher.find()) {
            stringBuilder.append(matcher.group("version").replace(".", ""));
            final String patch = matcher.group("patch");
            if (patch == null) stringBuilder.append("0");
            else stringBuilder.append(patch.replace(".", ""));
        }

        //noinspection UnstableApiUsage
        final Integer version = Ints.tryParse(stringBuilder.toString());

        // Should never fail
        if (version == null) throw new ChatChatException("Could not retrieve server version!");

        return version;
    }

    private static String getNmsVersion() {
        final String version = Bukkit.getServer().getClass().getPackage().getName();
        return version.substring(version.lastIndexOf('.') + 1);
    }

    public static Class<?> craftClass(@NotNull final String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + NMS_VERSION + "." + name);
    }

}
