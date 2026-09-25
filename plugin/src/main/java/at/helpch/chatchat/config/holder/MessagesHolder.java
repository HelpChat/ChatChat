package at.helpch.chatchat.config.holder;

import net.kyori.adventure.text.minimessage.MiniMessage;
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

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    // user related
    private String consoleOnly = MINI_MESSAGE.serialize(text("Only the console can do this!", RED));
    private String playersOnly = MINI_MESSAGE.serialize(text("Only players can do this!", RED));
    private String userOffline = MINI_MESSAGE.serialize(text("The user is not online!", RED));
    private String playerNotFound = MINI_MESSAGE.serialize(text("That player has not joined this server!", RED));
    private String userNotInTown = MINI_MESSAGE.serialize(text("You are not in a town!", RED));

    // messaging related
    private String noReplies = MINI_MESSAGE.serialize(text("You have no one to reply to!", RED));
    private String repliesDisabled = MINI_MESSAGE.serialize(text("You can't send private messages while they're disabled!", RED));
    private String targetRepliesDisabled = MINI_MESSAGE.serialize(text("This user has their private messages disabled!", RED));
    private String privateMessagesEnabled = MINI_MESSAGE.serialize(text("Your private messages have been enabled!", GREEN));
    private String privateMessagesDisabled = MINI_MESSAGE.serialize(text("Your private messages have been disabled!", RED));
    private String cantMessageYourself = MINI_MESSAGE.serialize(text("You can't message yourself!", RED));
    private String emptyMessage = MINI_MESSAGE.serialize(text("You can't send an empty message!", RED));
    private String specialCharactersNoPermission = MINI_MESSAGE.serialize(text("You do not have permission to use special characters!",
        RED));
    private String invalidMessage = MINI_MESSAGE.serialize(text("The message you sent is invalid!", RED));
    private String socialSpyEnabled = MINI_MESSAGE.serialize(text("Social spy enabled", GREEN));
    private String socialSpyDisabled = MINI_MESSAGE.serialize(text("Social spy disabled", RED));
    private String ignoredPlayer = MINI_MESSAGE.serialize(text("Successfully ignored <player>.", GREEN));
    private String unignoredPlayer = MINI_MESSAGE.serialize(text("Successfully un-ignored <player>.", GREEN));
    private String alreadyIgnored = MINI_MESSAGE.serialize(text("You are already ignoring <player>.", RED));
    private String notIgnored = MINI_MESSAGE.serialize(text("You are not ignoring <player>.", RED));
    private String notIgnoringAnyone = MINI_MESSAGE.serialize(text("You are not ignoring anyone.", RED));
    private String ignoredPlayersList = MINI_MESSAGE.serialize(text("You are ignoring following players:", YELLOW)
        .append(text("<ignored_players>", WHITE))
        .append(text(".", YELLOW)));
    private String cantIgnoreYourself = MINI_MESSAGE.serialize(text("You cannot ignore yourself!", RED));
    private String cantSeparateSelf = MINI_MESSAGE.serialize(text("You cannot separate a player from themselves!", RED));
    private String separatedPlayers = MINI_MESSAGE.serialize(text("Separated <player1> and <player2>.", GREEN));
    private String unseparatedPlayers = MINI_MESSAGE.serialize(text("Removed the separation between <player1> and <player2>.", GREEN));
    private String alreadySeparated = MINI_MESSAGE.serialize(text("These players are already separated.", RED));
    private String notSeparated = MINI_MESSAGE.serialize(text("These players are not separated.", RED));
    private String separationLocked = MINI_MESSAGE.serialize(text("A staff-enforced separation prevents you from unignoring <player>.", RED));
    private String cantMessageIgnoredPlayer = MINI_MESSAGE.serialize(text("You cannot message a player who you ignore.", RED));
    private String cantMessageGeneral = MINI_MESSAGE.serialize(text("You cannot message this player.", RED));
    private String chatEnabledSuccessfully = MINI_MESSAGE.serialize(text("Your chat has been enabled successfully!", GREEN));
    private String chatDisabledSuccessfully = MINI_MESSAGE.serialize(text("Your chat has been disabled successfully!", RED));
    private String chatDisabled = MINI_MESSAGE.serialize(text("Your chat is disabled! You can not send or receive messages until you enable it.", RED));

    // channel related
    private String channelNoPermission = MINI_MESSAGE.serialize(text("You do not have permission to use this channel", RED));
    private String channelNoPermissionSwitch = MINI_MESSAGE.serialize(text("You no longer have permission to use this channel so it has been switched to the <default> channel. ", RED));
    private String channelSwitched = MINI_MESSAGE.serialize(text("You have switched to the <channel> channel", GREEN));

    private String rangedChatEnabledSuccessfully = MINI_MESSAGE.serialize(text("Your ranged chat has been enabled successfully!", GREEN));
    private String rangedChatDisabledSuccessfully = MINI_MESSAGE.serialize(text("Your ranged chat has been disabled successfully!", RED));

    // command related
    private String commandUnknownCommand = MINI_MESSAGE.serialize(text("Unknown Command.", RED));
    private String commandInvalidUsage = MINI_MESSAGE.serialize(text("Invalid usage.", RED));
    private String commandInvalidArgument = MINI_MESSAGE.serialize(text("Invalid argument.", RED));
    private String commandNoPermission = MINI_MESSAGE.serialize(text("No Permission.", RED));

    // dump reloated
    private String dumpFailed = MINI_MESSAGE.serialize(text("Failed to create dump!", RED));
    private String dumpSuccess = MINI_MESSAGE.serialize(text("Dump created successfully! You can find it at: <url>", GREEN));

    // mention related
    private String personalMentionsEnabled = MINI_MESSAGE.serialize(text("Successfully enabled personal mentions!", GREEN));
    private String personalMentionsDisabled = MINI_MESSAGE.serialize(text("Successfully disabled personal mentions!", GREEN));
    private String channelMentionsEnabled = MINI_MESSAGE.serialize(text("Successfully enabled channel mentions!", GREEN));
    private String channelMentionsDisabled = MINI_MESSAGE.serialize(text("Successfully disabled channel mentions!", GREEN));

    // format related
    private String invalidFormat = MINI_MESSAGE.serialize(text("Invalid format.", RED));

    // generic
    private String genericError = MINI_MESSAGE.serialize(text("An unexpected error occurred!", RED));

    public @NotNull String consoleOnly() {
        return consoleOnly;
    }

    public @NotNull String playersOnly() {
        return playersOnly;
    }

    public @NotNull String userOffline() {
        return userOffline;
    }

    public @NotNull String playerNotFound() {
        return playerNotFound;
    }

    public @NotNull String userNotInTown() {
        return userNotInTown;
    }

    public @NotNull String noReplies() {
        return noReplies;
    }

    public @NotNull String repliesDisabled() {
        return repliesDisabled;
    }

    public @NotNull String targetRepliesDisabled() {
        return targetRepliesDisabled;
    }

    public @NotNull String cantMessageYourself() {
        return cantMessageYourself;
    }

    public @NotNull String emptyMessage() {
        return emptyMessage;
    }

    public @NotNull String privateMessagesEnabled() {
        return privateMessagesEnabled;
    }

    public @NotNull String privateMessagesDisabled() {
        return privateMessagesDisabled;
    }

    public @NotNull String specialCharactersNoPermission() {
        return specialCharactersNoPermission;
    }

    public @NotNull String invalidMessage() {
        return invalidMessage;
    }

    public @NotNull String socialSpyEnabled() {
        return socialSpyEnabled;
    }

    public @NotNull String socialSpyDisabled() {
        return socialSpyDisabled;
    }

    public @NotNull String channelNoPermission() {
        return channelNoPermission;
    }

    public @NotNull String channelNoPermissionSwitch() {
        return channelNoPermissionSwitch;
    }

    public @NotNull String channelSwitched() {
        return channelSwitched;
    }

    public @NotNull String unknownCommand() {
        return commandUnknownCommand;
    }

    public @NotNull String invalidUsage() {
        return commandInvalidUsage;
    }

    public @NotNull String invalidArgument() {
        return commandInvalidArgument;
    }

    public @NotNull String noPermission() {
        return commandNoPermission;
    }

    public @NotNull String dumpFailed() {
        return dumpFailed;
    }

    public @NotNull String dumpSuccess() {
        return dumpSuccess;
    }

    public @NotNull String ignoredPlayer() {
        return ignoredPlayer;
    }

    public @NotNull String unignoredPlayer() {
        return unignoredPlayer;
    }

    public @NotNull String alreadyIgnored() {
        return alreadyIgnored;
    }

    public @NotNull String notIgnored() {
        return notIgnored;
    }

    public @NotNull String notIgnoringAnyone() {
        return notIgnoringAnyone;
    }

    public @NotNull String ignoredPlayersList() {
        return ignoredPlayersList;
    }

    public @NotNull String cantIgnoreYourself() {
        return cantIgnoreYourself;
    }

    public @NotNull String cantSeparateSelf() {
        return cantSeparateSelf;
    }

    public @NotNull String separatedPlayers() {
        return separatedPlayers;
    }

    public @NotNull String unseparatedPlayers() {
        return unseparatedPlayers;
    }

    public @NotNull String alreadySeparated() {
        return alreadySeparated;
    }

    public @NotNull String notSeparated() {
        return notSeparated;
    }

    public @NotNull String separationLocked() {
        return separationLocked;
    }

    public @NotNull String personalMentionsEnabled() {
        return personalMentionsEnabled;
    }

    public @NotNull String personalMentionsDisabled() {
        return personalMentionsDisabled;
    }

    public @NotNull String channelMentionsEnabled() {
        return channelMentionsEnabled;
    }

    public @NotNull String channelMentionsDisabled() {
        return channelMentionsDisabled;
    }

    public @NotNull String invalidFormat() {
        return invalidFormat;
    }

    public @NotNull String cantMessageIgnoredPlayer() {
        return cantMessageIgnoredPlayer;
    }

    public @NotNull String cantMessageGeneral() {
        return cantMessageGeneral;
    }

    public @NotNull String chatEnabledSuccessfully() {
        return chatEnabledSuccessfully;
    }

    public @NotNull String chatDisabledSuccessfully() {
        return chatDisabledSuccessfully;
    }

    public @NotNull String chatDisabled() {
        return chatDisabled;
    }


    public @NotNull String rangedChatEnabledSuccessfully() {
        return rangedChatEnabledSuccessfully;
    }

    public @NotNull String rangedChatDisabledSuccessfully() {
        return rangedChatDisabledSuccessfully;
    }

    public @NotNull String genericError() {
        return genericError;
    }
}
