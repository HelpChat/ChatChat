package at.helpch.chatchat.channel;

import at.helpch.chatchat.api.Channel;
import at.helpch.chatchat.api.User;

import java.util.Collections;
import java.util.List;

public final class ChatChannel implements Channel {

    private String name = "";

    private String messagePrefix = "";

    private String toggleCommand = "";

    private String channelPrefix = "";

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public String messagePrefix() {
        return messagePrefix;
    }

    @Override
    public String channelPrefix() {
        return channelPrefix;
    }

    @Override
    public List<User> usersInChannel() {
        return Collections.emptyList();
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public String commandName() {
        return null;
    }
}
