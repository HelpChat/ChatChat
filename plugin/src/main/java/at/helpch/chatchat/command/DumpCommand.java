package at.helpch.chatchat.command;

import at.helpch.chatchat.ChatChatPlugin;
import at.helpch.chatchat.api.user.User;
import at.helpch.chatchat.util.DumpUtils;
import at.helpch.chatchat.locale.LocaleMessage;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Optional;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

@Command("chatdump")
public class DumpCommand extends BaseCommand {

    private static final String DUMP_PERMISSION = "chatchat.dump";
    private static final TextReplacementConfig.Builder DUMP_REPLACEMENT_BUILDER = TextReplacementConfig.builder()
        .match("<url>");

    private final ChatChatPlugin plugin;

    public DumpCommand(@NotNull final ChatChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void dump(final User user, final @Suggestion("files") @Optional String file) {
        if (!user.hasPermission(DUMP_PERMISSION)) {
            plugin.sendConfiguredMessage(user, LocaleMessage.COMMAND_NO_PERMISSION);
            return;
        }

        final var dump = file != null
            ? DumpUtils.createDump(plugin, file)
            : DumpUtils.createDump(plugin, null);

        if (dump.isEmpty()) {
            plugin.sendConfiguredMessage(user, LocaleMessage.DUMP_FAILED);
            return;
        }

        DumpUtils.postDump(dump.get()).whenComplete((url, throwable) -> Bukkit.getScheduler().runTask(plugin, () -> {
            if (throwable != null) {
                plugin.sendConfiguredMessage(user, LocaleMessage.DUMP_FAILED);
                throwable.printStackTrace();
                return;
            }

            final var clickableUrl = Component.text(url)
                    .clickEvent(ClickEvent.openUrl(url));

            user.sendMessage(plugin.parseConfiguredMessage(user, LocaleMessage.DUMP_SUCCESS)
                .replaceText(DUMP_REPLACEMENT_BUILDER.replacement(clickableUrl).build()));
        }));
    }
}
