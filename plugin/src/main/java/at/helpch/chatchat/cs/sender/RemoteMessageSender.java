package at.helpch.chatchat.cs.sender;

public interface RemoteMessageSender {
    boolean send(String channel, String message);

    boolean sendPrivateMessage(PrivateMessagePayload payload);

    boolean sendPrivateMessageAck(String requestId, boolean delivered);

    record PrivateMessagePayload(
        String requestId,
        String senderUuid,
        String senderName,
        String recipientName,
        String rawMessage,
        boolean reply,
        boolean ignoreBypass,
        String recipientMessage,
        String socialSpyMessage
    ) {
    }
}
