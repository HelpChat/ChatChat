package at.helpch.chatchat.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Format {

    int getPriority();

    @NotNull
    List<String> getParts();

}
