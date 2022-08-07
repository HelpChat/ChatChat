package at.helpch.chatchat.config.holders;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

// configurate requires non-final fields
@SuppressWarnings("FieldMayBeFinal")
@ConfigSerializable
public final class MessagesHolder {

    // user related
    private Component consoleOnly = text("Only the console can do this!", RED);
    private Component playersOnly = text("Only players can do this!", RED);
    private Component userOffline = text("The user is not online!", RED);
    private Component userNotInTown = text("You are not in a town!", RED);

    // messaging related
    private Component noReplies = text("You have no one to reply to!", RED);
    private Component repliesDisabled = text("You can't send private messages while they're disabled!", RED);
    private Component targetRepliesDisabled = text("This user has their private messages disabled!", RED);
    private Component privateMessagesEnabled = text("Your private messages have been enabled!", GREEN);
    private Component privateMessagesDisabled = text("Your private messages have been disabled!", RED);
    private Component cantMessageYourself = text("You can't message yourself!", RED);
    private Component emptyMessage = text("You can't send an empty message!", RED);
    private Component specialCharactersNoPermission = text("You do not have permission to use special characters!",
        RED);
    private Component socialSpyEnabled = text("Social spy enabled", GREEN);
    private Component socialSpyDisabled = text("Social spy disabled", RED);

    // channel related
    private Component channelNoPermission = text("You do not have permission to use this channel", RED);
    private Component channelSwitched = text("You have switched to the <channel> channel", GREEN);

    // command related
    private Component commandUnknownCommand = text("Unknown Command.", RED);
    private Component commandInvalidUsage = text("Invalid usage.", RED);
    private Component commandInvalidArgument = text("Invalid argument.", RED);
    private Component commandNoPermission = text("No Permission.", RED);

    // mention related
    private Component mentionsEnabled = text("Successfully enabled mentions!", GREEN);
    private Component mentionsDisabled = text("Successfully disabled mentions!", GREEN);

    public @NotNull Component consoleOnly() {
        return consoleOnly;
    }

    public @NotNull Component playersOnly() {
        return playersOnly;
    }

    public @NotNull Component userOffline() {
        return userOffline;
    }

    public @NotNull Component userNotInTown() {
        return userNotInTown;
    }

    public @NotNull Component noReplies() {
        return noReplies;
    }

    public @NotNull Component repliesDisabled() {
        return repliesDisabled;
    }

    public @NotNull Component targetRepliesDisabled() {
        return targetRepliesDisabled;
    }

    public @NotNull Component cantMessageYourself() {
        return cantMessageYourself;
    }

    public @NotNull Component emptyMessage() {
        return emptyMessage;
    }

    public @NotNull Component privateMessagesEnabled() {
        return privateMessagesEnabled;
    }

    public @NotNull Component privateMessagesDisabled() {
        return privateMessagesDisabled;
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

    public @NotNull Component mentionsEnabled() {
        return mentionsEnabled;
    }

    public @NotNull Component mentionsDisabled() {
        return mentionsDisabled;
    }
}
