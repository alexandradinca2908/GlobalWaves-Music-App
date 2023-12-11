package main.SelectionClasses.Creators;

import fileio.input.UserInput;

public final class HostSelection {
    private UserInput host;

    public HostSelection() {
    }

    public UserInput getHost() {
        return host;
    }

    public void setHost(UserInput host) {
        this.host = host;
    }
}
