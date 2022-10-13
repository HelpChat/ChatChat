package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.User;
import at.helpch.chatchat.util.DumpUtils;
import dev.triumphteam.cmd.core.annotation.Optional;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;

public class DumpCommand extends ChatChatCommand {

    private static final String DUMP_PERMISSION = "chatchat.dump";
    private static final TextReplacementConfig.Builder DUMP_REPLACEMENT_BUILDER = TextReplacementConfig.builder()
        .match("<url>");

    private final ChatChatPlugin plugin;

    public DumpCommand(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @SubCommand("dump")
    public void dump(final User user, final @Suggestion("files") @Optional String file) {
        if (!user.hasPermission(DUMP_PERMISSION)) {
            user.sendMessage(plugin.configManager().messages().noPermission());
            return;
        }

        final var dump = file != null
            ? DumpUtils.createDump(plugin, file)
            : DumpUtils.createDump(plugin, null);

        if (dump.isEmpty()) {
            user.sendMessage(plugin.configManager().messages().dumpFailed());
            return;
        }

        DumpUtils.postDump(dump.get()).whenComplete((url, throwable) -> {
            if (throwable != null) {
                user.sendMessage(plugin.configManager().messages().dumpFailed());
                throwable.printStackTrace();
                return;
            }

            final var clickableUrl = Component.text(url)
                    .clickEvent(ClickEvent.openUrl(url));

            user.sendMessage(plugin.configManager().messages().dumpSuccess()
                .replaceText(DUMP_REPLACEMENT_BUILDER.replacement(clickableUrl).build()));
        });
    }
}
