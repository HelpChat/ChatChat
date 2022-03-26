package at.helpch.chatchat.config.holders;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

// configurate requires non-final fields
@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public final class MessagesHolder {

    // messaging related
    private Component noReplies = text("You have no one to reply to!", RED);
    private Component cantMessageYourself = text("You can't message yourself!", RED);
    private Component specialCharactersNoPermission = text("You do not have permission to use special characters!", RED);
    private Component socialSpyEnabled = text("Social spy enabled", GREEN);
    private Component socialSpyDisabled = text("Social spy disabled", RED);

    // channel related
    private Component channelNoPermission = text("You do not have permission to use this channel", RED);
    private Component channelSwitched = text("You have switched to the %channel% channel", GREEN);

    // command related
    private Component commandUnknownCommand = text("Unknown Command.", RED);
    private Component commandInvalidUsage = text("Invalid usage.", RED);
    private Component commandInvalidArgument = text("Invalid argument.", RED);
    private Component commandNoPermission = text("No Permission.", RED);

    public @NotNull Component noReplies() {
        return noReplies;
    }

    public @NotNull Component cantMessageYourself() {
        return cantMessageYourself;
    }

    public @NotNull Component specialCharactersNoPermission() {
        return specialCharactersNoPermission;
    }

    public @NotNull Component socialSpyEnabled() {
        return socialSpyEnabled;
    }

    public @NotNull Component socialSpyDisabled() {
        return socialSpyDisabled;
    }

    public @NotNull Component channelNoPermission() {
        return channelNoPermission;
    }

    public @NotNull Component channelSwitched() {
        return channelSwitched;
    }

    public @NotNull Component unknownCommand() {
        return commandUnknownCommand;
    }

    public @NotNull Component invalidUsage() {
        return commandInvalidUsage;
    }

    public @NotNull Component invalidArgument() {
        return commandInvalidArgument;
    }

    public @NotNull Component noPermission() {
        return commandNoPermission;
    }
}
