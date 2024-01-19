package at.helpch.chatchat.cs.sender;

public interface RemoteMessageSender {
    boolean send(String channel, String message);
}
