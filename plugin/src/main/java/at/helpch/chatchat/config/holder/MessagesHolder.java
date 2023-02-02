package at.helpch.chatchat.config.holder;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

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
    private Component invalidMessage = text("The message you sent is invalid!", RED);
    private Component socialSpyEnabled = text("Social spy enabled", GREEN);
    private Component socialSpyDisabled = text("Social spy disabled", RED);
    private Component ignoredPlayer = text("Successfully ignored <player>.", GREEN);
    private Component unignoredPlayer = text("Successfully un-ignored <player>.", GREEN);
    private Component alreadyIgnored = text("You are already ignoring <player>.", RED);
    private Component notIgnored = text("You are not ignoring <player>.", RED);
    private Component notIgnoringAnyone = text("You are not ignoring anyone.", RED);
    private Component ignoredPlayersList = text("You are ignoring following players:", YELLOW)
        .append(text("<ignored_players>", WHITE))
        .append(text(".", YELLOW));
    private Component cantIgnoreYourself = text("You cannot ignore yourself!", RED);
    private Component cantMessageIgnoredPlayer = text("You cannot message a player who you ignore.", RED);
    private Component cantMessageGeneral = text("You cannot message this player.", RED);

    // channel related
    private Component channelNoPermission = text("You do not have permission to use this channel", RED);
    private Component channelNoPermissionSwitch = text("You no longer have permission to use this channel so it has been switched to the <default> channel. ", RED);
    private Component channelSwitched = text("You have switched to the <channel> channel", GREEN);

    // command related
    private Component commandUnknownCommand = text("Unknown Command.", RED);
    private Component commandInvalidUsage = text("Invalid usage.", RED);
    private Component commandInvalidArgument = text("Invalid argument.", RED);
    private Component commandNoPermission = text("No Permission.", RED);

    // dump reloated
    private Component dumpFailed = text("Failed to create dump!", RED);
    private Component dumpSuccess = text("Dump created successfully! You can find it at: <url>", GREEN);

    // mention related
    private Component personalMentionsEnabled = text("Successfully enabled personal mentions!", GREEN);
    private Component personalMentionsDisabled = text("Successfully disabled personal mentions!", GREEN);
    private Component channelMentionsEnabled = text("Successfully enabled channel mentions!", GREEN);
    private Component channelMentionsDisabled = text("Successfully disabled channel mentions!", GREEN);

    // format related
    private Component invalidFormat = text("Invalid format.", RED);

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

    public @NotNull Component invalidMessage() {
        return invalidMessage;
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

    public @NotNull Component channelNoPermissionSwitch() { return channelNoPermissionSwitch; }

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

    public @NotNull Component dumpFailed() {
        return dumpFailed;
    }

    public @NotNull Component dumpSuccess() {
        return dumpSuccess;
    }

    public @NotNull Component ignoredPlayer() {
        return ignoredPlayer;
    }

    public @NotNull Component unignoredPlayer() {
        return unignoredPlayer;
    }

    public @NotNull Component alreadyIgnored() {
        return alreadyIgnored;
    }

    public @NotNull Component notIgnored() {
        return notIgnored;
    }

    public @NotNull Component notIgnoringAnyone() {
        return notIgnoringAnyone;
    }

    public @NotNull Component ignoredPlayersList() {
        return ignoredPlayersList;
    }

    public @NotNull Component cantIgnoreYourself() {
        return cantIgnoreYourself;
    }

    public @NotNull Component personalMentionsEnabled() {
        return personalMentionsEnabled;
    }

    public @NotNull Component personalMentionsDisabled() {
        return personalMentionsDisabled;
    }

    public @NotNull Component channelMentionsEnabled() {
        return channelMentionsEnabled;
    }

    public @NotNull Component channelMentionsDisabled() {
        return channelMentionsDisabled;
    }

    public @NotNull Component invalidFormat() {
        return invalidFormat;
    }

    public @NotNull Component cantMessageIgnoredPlayer() {
        return cantMessageIgnoredPlayer;
    }

    public @NotNull Component cantMessageGeneral() {
        return cantMessageGeneral;
    }

}
