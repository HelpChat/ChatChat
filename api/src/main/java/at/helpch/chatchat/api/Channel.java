package at.helpch.chatchat.api;

import java.util.List;

public interface Channel {

    boolean isDefault();

    String messagePrefix();

    String channelPrefix();

    List<User> usersInChannel();

    String name();

    String commandName();
}
