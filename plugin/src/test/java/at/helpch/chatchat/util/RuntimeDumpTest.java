package at.helpch.chatchat.util;

import at.helpch.chatchat.api.channel.Channel;
import at.helpch.chatchat.api.holder.FormatsHolder;
import at.helpch.chatchat.api.user.ChatUser;
import at.helpch.chatchat.api.user.User;
import at.helpch.chatchat.config.holder.FormatsHolderImpl;
import at.helpch.chatchat.format.ChatFormat;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public final class RuntimeDumpTest {

    @Test
    public void reportsActiveFormatsAndPreservesPartOrder() {
        final var parts = new LinkedHashMap<String, List<String>>();
        parts.put("first", List.of("<red>Hello"));
        parts.put("second", List.of("<message>"));
        final var active = new ChatFormat("display", 7, parts);
        final var builder = new StringBuilder();

        RuntimeDump.appendFormats(builder, Map.of("configured", active), "configured", active);

        final var dump = builder.toString();
        assertTrue(dump.contains("Active default format: \"display\""));
        assertTrue(dump.contains("registered=true"));
        assertTrue(dump.contains("key=\"configured\", name=\"display\""));
        assertTrue(dump.contains("priority=7, default=true"));
        assertTrue(dump.indexOf("\"first\" =") < dump.indexOf("\"second\" ="));
    }

    @Test
    public void reportsLoadedChannelsAndAnInternalFallback() {
        final var channelFormat = new ChatFormat("staff-format", 3, Map.of("message", List.of("<message>")));
        final var configured = new TestChannel("staff", "!", "<red>[staff]", List.of("staff"),
            new FormatsHolderImpl(Map.of("staff-format", channelFormat)), 50, true);
        final var fallback = new TestChannel("fallback", "", "[global]", List.of("global"),
            new FormatsHolderImpl(), -1, false);
        final var builder = new StringBuilder();

        RuntimeDump.appendChannels(builder, Map.of("staff-key", configured), "missing", fallback);

        final var dump = builder.toString();
        assertTrue(dump.contains("Configured default channel: \"missing\""));
        assertTrue(dump.contains("Active default channel: \"fallback\""));
        assertTrue(dump.contains("registered=false"));
        assertTrue(dump.contains("key=\"staff-key\", name=\"staff\""));
        assertTrue(dump.contains("commands=[\"staff\"], radius=50, cross-server=true"));
        assertTrue(dump.contains("Channel formats (1)"));
        assertTrue(dump.contains("Internal fallback channel:"));
    }

    private record TestChannel(String name, String messagePrefix, String channelPrefix,
                               List<String> commandNames, FormatsHolder formats, int radius,
                               boolean crossServer) implements Channel {

        @Override
        public Set<User> targets(final User source) {
            return Set.of();
        }

        @Override
        public boolean isUsableBy(final ChatUser user) {
            return true;
        }
    }
}
